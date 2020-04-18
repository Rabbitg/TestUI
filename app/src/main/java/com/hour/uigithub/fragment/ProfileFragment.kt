package com.hour.uigithub.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.hour.uigithub.R
import com.hour.uigithub.util.toast
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"

    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser?.let {user ->
            // 포토 URL 가져와서 이미지 뷰를 처리 해주는데
            // 글라이드는 이미지 처리를 잘해주는 것이라고 생각하면 된다.
            Glide.with(this)
                .load(user.photoUrl)
                .into(image_view)
            edit_text_name.setText(user.displayName)
            text_email.text = user.email
            text_phone.text = if(user.phoneNumber.isNullOrEmpty()) "휴대폰 번호를 추가해주세요." else user.phoneNumber

            if(user.isEmailVerified){
                text_not_verified.visibility = View.INVISIBLE
            }
            else{
                text_not_verified.visibility = View.VISIBLE
            }

        }


        image_view.setOnClickListener {
            takePictureIntent()
        }

        button_save.setOnClickListener {
            val photo = when{
                // 선택된 이미지를 사진에 사용한다.
                ::imageUri.isInitialized -> imageUri
                // 현재 사용자가 널이면
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                    else -> currentUser.photoUrl
            }

            val name = edit_text_name.text.toString().trim()

            if(name.isEmpty()){
                edit_text_name.error = "이름을 입력해주세요"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener{task ->
                    progressbar.visibility = View.INVISIBLE
                    if(task.isSuccessful){
                        context?.toast("프로파일 업데이트 했습니다.")
                    }else{
                        context?.toast(task.exception?.message!!)
                    }
                }
        }

        text_not_verified.setOnClickListener {
            currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    if (it.isSuccessful){
                        context?.toast("이메일을 확인 해주세요.")
                    }else{
                        context?.toast(it.exception?.message!!)
                    }

                }
        }

        text_phone.setOnClickListener {
            val action = ProfileFragmentDirections.actionVerifyPhone()
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun takePictureIntent() {
        //startActivityForResult() 메서드는 resolveActivity()를 호출하는 조건에 의해 보호되며
        // 이 함수는 인텐트를 처리할 수 있는 첫 번째 활동 구성요소를 반환합니다.
        // 이 확인 절차가 중요한 이유는 앱이 처리할 수 없는 인텐트를 사용하여 startActivityForResult()를 호출하면
        // 앱이 비정상 종료되기 때문입니다. 따라서 결과가 null이 아닌 한 안심하고 인텐트를 사용할 수 있습니다.
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Android 카메라 애플리케이션은 onActivityResult()에
            // 전달된 반환 Intent의 "data" 키 아래 extras에 작은 Bitmap으로 사진을 인코딩합니다.
            // 다음 코드는 이미지를 가져와서 ImageView에 표시하는 방법을 보여줍니다.
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {

        // ByteArrayOutputStream 은 메모리, 즉 바이트 배열에 데이터를 입출력하는데 사용되는 스트림이다
        val baos = ByteArrayOutputStream()
        // Storage 인스턴스 얻어와서 child() 에는 유저 정보를 받아와서 현재 유저정보를 받고 pics 라는 폴더에 저장한다.
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        // bitmap 압축 방식
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        // ByteArray 메소드를 이용하면 저장된 모든 내용이 바이트 배열로 반환
        val image = baos.toByteArray()
        // 스토리지 레퍼런스에 배열로 반환한 image 변수를 put !
        val upload = storageRef.putBytes(image)

        // 불러오는 동안 프로그레스바 로 보이도록 !
        progressbar_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            // 성공하면 프로그레스바 안보이도록
            progressbar_pic.visibility = View.INVISIBLE

            if (uploadTask.isSuccessful) {
                // 객체를 다운로드하는 데 사용할 수 있는 URL
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        activity?.toast(imageUri.toString())
                        image_view.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                }
            }
        }

    }

}
