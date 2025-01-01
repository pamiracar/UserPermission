package com.pamiracar.yemekkitabi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.pamiracar.yemekkitabi.databinding.FragmentListeBinding
import com.pamiracar.yemekkitabi.databinding.FragmentTarifBinding
import java.io.IOException

//before using codes, please read the README

class TarifFragment : Fragment() {

    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!
    private lateinit var permisionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var secilenGorsel : Uri? = null
    private var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTarifBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if (bilgi == "yeni"){
                //Yeni tarif gösterilecek
                binding.silButton.isEnabled = false
                binding.kaydetButton.isEnabled = true
                binding.yemekIsmiEditText.setText("")
                binding.yemekMalzemeEditText.setText("")
            }else{
                //Eski tarif gösterilecek
                binding.silButton.isEnabled = true
                binding.kaydetButton.isEnabled = false
            }
        }

        binding.yemekResim.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                    //İzin verilmemiş, izin istenmesi gerekiyor
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                        //izni neden istediğimizi snackbar ile göstermemiz lazım
                        Snackbar.make(view,"Görsel seçimi için galeriye ulaşma izni gerekmektedir.",Snackbar.LENGTH_INDEFINITE).setAction(
                            "İzin ver",
                            View.OnClickListener {
                                //izin isteyeceğiz
                                permisionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        ).show()
                    }else{
                        //izin isteyeceğiz
                        permisionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                    }
                }else{
                    //İzin verilmiş. Galeriye gidilebilir.
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }

            }else{
                if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //İzin verilmemiş, izin istenmesi gerekiyor
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                        //izni neden istediğimizi snackbar ile göstermemiz lazım
                        Snackbar.make(view,"Görsel seçimi için galeriye ulaşma izni gerekmektedir.",Snackbar.LENGTH_INDEFINITE).setAction(
                            "İzin ver",
                            View.OnClickListener {
                                //izin isteyeceğiz
                                permisionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        ).show()
                    }else{
                        //izin isteyeceğiz
                        permisionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }
                }else{
                    //İzin verilmiş. Galeriye gidilebilir.
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
            }
        }
    }

    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null){
                    secilenGorsel = intentFromResult.data

                    try {
                        if (Build.VERSION.SDK_INT >= 28){
                            //Sdk sı 28in üstü cihazlar
                            val source = ImageDecoder.createSource(requireActivity().contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.yemekResim.setImageBitmap(secilenBitmap)
                        } else{
                            // 28 in altı olanlar
                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.yemekResim.setImageBitmap(secilenBitmap)

                        }
                    }catch (e: Exception){
                        println(e.localizedMessage)
                    }

                }
            }
        }

        permisionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                //galeriye gidilebilir
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(requireContext(),"İzin Verilmedi",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//before using codes, please read the README

