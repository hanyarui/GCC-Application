package com.gcc.gccapplication.viewModel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.NotifikasiModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileViewModel: ViewModel(){

    private val _notifikasiData = MutableLiveData<List<NotifikasiModel>>()
    val notifikasiData: LiveData<List<NotifikasiModel>> get() = _notifikasiData

    private val _fileSavedLocation = MutableLiveData<String>()
    val fileSavedLocation: LiveData<String> get() = _fileSavedLocation

    private val db = FirebaseFirestore.getInstance()

    fun fetchAndExportDataToPdf(context: Context) {
        // Implementasi untuk fetch data dari Firestore dan ekspor ke PDF
        val buktiCollection = db.collection("buktiSampah")
        val angkutCollection = db.collection("angkut")
        val userCollection = db.collection("users")

        // Ambil alamat admin dari currentUser
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val currentUserQuery = userCollection.whereEqualTo("email", currentUserEmail).get()

        currentUserQuery.addOnSuccessListener { userDocs ->
            val adminAddress = userDocs.documents.firstOrNull()?.getString("address") ?: ""

            // Ambil semua dokumen dari buktiSampah
            buktiCollection.get()
                .addOnSuccessListener { buktiDocuments ->
                    Log.d("NotifikasiViewModel", "buktiDocuments size: ${buktiDocuments.size()}")
                    val notifikasiList = ArrayList<NotifikasiModel>()
                    val fetchTasks = ArrayList<Task<*>>()

                    for (buktiDoc in buktiDocuments) {
                        val buktiUploadId = buktiDoc.id
                        val namaLengkap = buktiDoc.getString("namaLengkap") ?: ""
                        val alamatLengkap = buktiDoc.getString("alamatLengkap") ?: ""
                        val noHp = buktiDoc.getString("noHp") ?: ""
                        val email = buktiDoc.getString("email") ?: ""
                        val isPicked = buktiDoc.getBoolean("isPicked") ?: false
                        val timestamp = buktiDoc.getTimestamp("timestamp")
                        val time = timestamp?.toDate().toString()
                        val timestampLong = timestamp?.toDate()?.time ?: 0L

                        // Ambil alamat dari user yang sesuai dengan email
                        userCollection.whereEqualTo("email", email).get()
                            .addOnSuccessListener { userDocs ->
                                val userAddress = userDocs.documents.firstOrNull()?.getString("address") ?: ""

                                // Cek jika alamat user sama dengan alamat admin
                                if (userAddress == adminAddress) {
                                    // Ambil data dari collection angkut
                                    val trashItemsQuery = angkutCollection.whereEqualTo("buktiUploadId", buktiUploadId).get()
                                    fetchTasks.add(trashItemsQuery)

                                    trashItemsQuery.addOnSuccessListener { trashItemsDocuments ->
                                        var totalAmount = 0.0

                                        for (trashItemDoc in trashItemsDocuments) {
                                            val amount = trashItemDoc.getString("amount")?.toDoubleOrNull() ?: 0.0
                                            totalAmount += amount
                                        }

                                        notifikasiList.add(
                                            NotifikasiModel(
                                                namaLengkap = namaLengkap,
                                                alamatLengkap = alamatLengkap,
                                                noHp = noHp,
                                                totalAmount = totalAmount.toString(),
                                                dusun = userAddress,
                                                time = time,
                                                timeStampLong = timestampLong,
                                                isPicked = isPicked,
                                                buktiUploadId = buktiUploadId
                                            )
                                        )
                                        exportDataToPdf(notifikasiList)

                                    }
                                        .addOnFailureListener { e ->
                                            Log.e("NotifikasiViewModel", "Failed to fetch trash items for $buktiUploadId: ${e.message}")
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("NotifikasiViewModel", "Failed to fetch user data for email $email: ${e.message}")
                            }
                    }

                    Tasks.whenAll(fetchTasks).addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Log.e("NotifikasiViewModel", "Failed to fetch all trash items: ${it.exception?.message}")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    _notifikasiData.value = emptyList()
                    Log.e("NotifikasiViewModel", "Failed to fetch buktiSampah data: ${e.message}")
                }
        }
            .addOnFailureListener { e ->
                Log.e("NotifikasiViewModel", "Failed to fetch currentUser data: ${e.message}")
            }
    }

    private fun exportDataToPdf(notifikasiList: List<NotifikasiModel>) {
        val pdfDocument = PdfDocument()
        var pageNumber = 1
        var yPos = 40
        val pageHeight = 842 // Tinggi halaman dalam piksel (A4)
        val margin = 40 // Margin atas dan bawah
        val pageWidth = 595 // Lebar halaman dalam piksel

        fun startNewPage(page: PdfDocument.Page): PdfDocument.Page {
            if (yPos > pageHeight - margin) {
                pdfDocument.finishPage(page) // Finish the current page
                pageNumber++
                yPos = 40 // Reset yPos for new page
                val newPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                val newPage = pdfDocument.startPage(newPageInfo)
                return newPage // Return the new page
            }
            return page // Return the existing page if no new page is needed
        }

        fun drawCenteredText(text: String, yPos: Int, paint: Paint, canvas: Canvas) {
            val textWidth = paint.measureText(text)
            val xPos = (pageWidth - textWidth) / 2 // Pusatkan secara horizontal
            canvas.drawText(text, xPos, yPos.toFloat(), paint)
        }

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
        }
        val boldPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Set teks bold
            textSize = 16f // Atur ukuran teks sesuai kebutuhan
        }
        drawCenteredText("Data Rekap Sampah Dusun ${notifikasiList.firstOrNull()?.dusun}", yPos, boldPaint, canvas)
        yPos += 10
        canvas.drawLine(10f, yPos.toFloat(), 585f, yPos.toFloat(), linePaint)
        yPos += 20

        for (notifikasi in notifikasiList) {
            // Ensure that we have enough space on the current page or start a new page
            page = startNewPage(page)
            canvas = page.canvas

            canvas.drawText("Nama Lengkap: ${notifikasi.namaLengkap}", 20f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Alamat Lengkap: ${notifikasi.alamatLengkap}", 20f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Nomor Handphone: ${notifikasi.noHp}", 20f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Dusun: ${notifikasi.dusun}", 20f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Jumlah Sampah: ${notifikasi.totalAmount}", 20f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Waktu: ${notifikasi.time}", 20f, yPos.toFloat(), paint)
            yPos += 20

            if (notifikasi.isPicked == true) {
                canvas.drawText("Status pengambilan sampah: Sudah di ambil", 20f, yPos.toFloat(), paint)

            } else {
                canvas.drawText("Status pengambilan sampah: Belum di ambil", 20f, yPos.toFloat(), paint)

            }
            yPos += 10

            // Tambahkan garis di akhir data
            canvas.drawLine(10f, yPos.toFloat(), 585f, yPos.toFloat(), linePaint)
            yPos +=20
        }

        pdfDocument.finishPage(page)

        val dir = File("/storage/emulated/0/Documents/RekapGCC/")
        if (!dir.exists()) {
            dir.mkdirs() // Membuat direktori jika belum ada
        }

        val file = File(dir, "RekapSampah.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Log.d("ProfileFragment", "PDF berhasil disimpan di ${file.absolutePath}")
            _fileSavedLocation.postValue(file.absolutePath)  // Update LiveData
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ProfileFragment", "Gagal menyimpan PDF: ${e.message}")
//            _fileSavedLocation.postValue(null)  // Indicate failure
//        } finally {
            pdfDocument.close()
        }
    }

}