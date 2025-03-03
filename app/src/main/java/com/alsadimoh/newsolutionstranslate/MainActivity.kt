package com.alsadimoh.newsolutionstranslate

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.alsadimoh.newsolutionstranslate.adapter.LanguagesAdapter
import com.alsadimoh.newsolutionstranslate.databinding.ActivityMainBinding
import com.alsadimoh.newsolutionstranslate.databinding.CustomChooseLanguageDialogBinding
import com.alsadimoh.newsolutionstranslate.room.DatabaseBuilder
import com.alsadimoh.newsolutionstranslate.room.helper.DatabaseHelperImpl
import com.alsadimoh.newsolutionstranslate.room.utils.Status
import com.alsadimoh.newsolutionstranslate.room.utils.ViewModelFactory
import com.alsadimoh.newsolutionstranslate.room.viewModel.RoomViewModel
import com.alsadimoh.newsolutionstranslate.common.Common.TAG
import com.alsadimoh.newsolutionstranslate.common.Common.checkCameraPermissions
import com.alsadimoh.newsolutionstranslate.common.Common.checkReadStoragePermissions
import com.alsadimoh.newsolutionstranslate.common.Common.clipboardManager
import com.alsadimoh.newsolutionstranslate.common.Common.copyToClipboard
import com.alsadimoh.newsolutionstranslate.common.Common.getLanguageCode
import com.alsadimoh.newsolutionstranslate.common.Common.getLanguageFromSubname
import com.alsadimoh.newsolutionstranslate.common.Common.getSubnameFromLanguageCode
import com.alsadimoh.newsolutionstranslate.common.Common.myShared
import com.alsadimoh.newsolutionstranslate.common.Common.pasteFromClipboard
import com.alsadimoh.newsolutionstranslate.common.Common.resultTypeCamera
import com.alsadimoh.newsolutionstranslate.common.Common.resultTypeGallery
import com.alsadimoh.newsolutionstranslate.common.Common.resultTypeRecordAudio
import com.alsadimoh.newsolutionstranslate.common.Common.savedFromLang
import com.alsadimoh.newsolutionstranslate.common.Common.savedToLang
import com.alsadimoh.newsolutionstranslate.common.Common.sharedPrefKey
import com.alsadimoh.newsolutionstranslate.common.Common.showExpandCardDialog
import com.alsadimoh.newsolutionstranslate.common.Common.speakTextTTS
import com.alsadimoh.newsolutionstranslate.common.Common.textToSpeech
import com.alsadimoh.newsolutionstranslate.common.FavoriteModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var languagesFrom:Array<String>
    private lateinit var languagesTo:Array<String>
    private val defaultLangCode = -1
    private var fromLanguageCode = defaultLangCode
    private var toLanguageCode = defaultLangCode
    private var lastFromLanguagePosition = 0
    private var lastToLanguagePosition = 0
    private lateinit var dialog : Dialog
    private var copiedText :CharSequence? = ""
    private var isTranslatedSuccessfully = false
    private lateinit var textRecognizerFromImage: TextRecognizer
    private lateinit var imageUri: Uri
    private lateinit var bitmap: Bitmap
    private var resultType = ""
    private var isFavorite = false
    private lateinit var roomViewModel:RoomViewModel


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        myShared = getSharedPreferences(sharedPrefKey,Context.MODE_PRIVATE)
        setContentView(binding.root)

        delegate.isHandleNativeActionModesEnabled = false

        //val isEditable = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        /*val intent = Intent()
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "New Text To Replace")
        setResult(RESULT_OK, intent)
        //finish()
        */

        initViewModel()
        observeGetInsertFavorite()
        observeDeleteFavorite()
        observeCheckFavorite()

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                when (resultType){
                    resultTypeRecordAudio-> {
                        val resultArray =
                            result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        if (!resultArray.isNullOrEmpty()) {
                            binding.txtToTranslate.setText(resultArray[0])
                        }
                    }
                    resultTypeGallery -> {
                        if (result.data?.data != null) {
                            imageUri = result.data?.data!!
                            recognizeTextFromImage(resultTypeGallery)
                        }
                    }
                    resultTypeCamera -> {
                        bitmap = result.data?.extras?.get("data") as Bitmap
                        recognizeTextFromImage(resultTypeCamera)
                    }
                }
            }
        }


        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // PERMISSION GRANTED
                Log.e(TAG, "onCreateView: GRANTED" )
                if (resultType == resultTypeCamera){
                    val i =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    resultLauncher.launch(i)
                }else if (resultType == resultTypeGallery){
                    val i = Intent(Intent.ACTION_PICK)
                    i.type = "image/*"
                    resultLauncher.launch(i)
                }
            }
        }

        try {
            textToSpeech = TextToSpeech(this) {}
        }catch (_:java.lang.Exception){ }

        textRecognizerFromImage = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        binding.btnReadFromCamera.setOnClickListener {
            resultType = resultTypeCamera
            if (checkCameraPermissions(this)){
              val i =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(i)
            }else{
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnReadFromGallery.setOnClickListener {
            resultType = resultTypeGallery
            if (checkReadStoragePermissions(this)){
                val i = Intent(Intent.ACTION_PICK)
                i.type = "image/*"
                resultLauncher.launch(i)
            }else{
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // tts
        binding.btnTextFromTTS.setOnClickListener {
            speakTextTTS(this,binding.txtToTranslate.text.toString(), getSubnameFromLanguageCode(fromLanguageCode))
        }

        binding.btnTextToTTS.setOnClickListener {
            speakTextTTS(this,binding.txtTranslatedText.text.toString(), getSubnameFromLanguageCode(toLanguageCode))
        }

        binding.btnClearText.setOnClickListener {
            if (binding.txtToTranslate.text.isNotEmpty()){
                binding.txtToTranslate.text.clear()
            }
        }

        binding.btnSetAsFavourite.setOnClickListener {
            binding.btnSetAsFavourite.isEnabled = false
            if (isFavorite){
                roomViewModel.deleteFavorite(binding.txtToTranslate.text.toString(), getSubnameFromLanguageCode(toLanguageCode),getSubnameFromLanguageCode(fromLanguageCode))
            }else{
                if (isTranslatedSuccessfully){
                    roomViewModel.insertFavorite(FavoriteModel(getSubnameFromLanguageCode(fromLanguageCode), getSubnameFromLanguageCode(toLanguageCode),binding.txtToTranslate.text.toString(),binding.txtTranslatedText.text.toString(),System.currentTimeMillis()))
                }
            }
        }

        binding.btnExpandText.setOnClickListener {
            if (binding.txtTranslatedText.text.trim().isNotEmpty()){
               showExpandCardDialog(this,binding.txtTranslatedText.text.toString())
            }
        }

        // views default visibility
        if (binding.txtTranslatedText.text.isNullOrEmpty()){
            binding.btnCopy.visibility = View.GONE
            binding.btnTextToTTS.visibility = View.GONE
            binding.btnExpandText.visibility = View.GONE
            binding.btnSetAsFavourite.visibility = View.GONE
        }else{
            binding.btnCopy.visibility = View.VISIBLE
            binding.btnTextToTTS.visibility = View.VISIBLE
            binding.btnExpandText.visibility = View.VISIBLE
            binding.btnSetAsFavourite.visibility = View.VISIBLE
        }

        if (binding.txtToTranslate.text.isNullOrEmpty()){
            binding.btnTextFromTTS.visibility = View.GONE
            binding.btnClearText.visibility = View.GONE
        }else{
            binding.btnTextFromTTS.visibility = View.VISIBLE
            binding.btnClearText.visibility = View.VISIBLE
        }

        binding.txtTranslatedText.addTextChangedListener { text ->
            handleTranslatedTextChanged(text)
        }


        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        /// copy
        binding.btnCopy.setOnClickListener {
            copyToClipboard(this, binding.txtTranslatedText.text.toString())
        }

        /// paste
        copiedText = pasteFromClipboard(this)
        if (copiedText.isNullOrEmpty()){
            binding.btnPaste.visibility = View.GONE
        }else{
            binding.btnPaste.visibility = View.VISIBLE
        }

        clipboardManager?.addPrimaryClipChangedListener {
            copiedText = pasteFromClipboard(this)
            if (copiedText.isNullOrEmpty()){
                binding.btnPaste.visibility = View.GONE
            }else{
                binding.btnPaste.visibility = View.VISIBLE
            }
        }

        binding.btnPaste.setOnClickListener {
            if (!copiedText.isNullOrEmpty()){
                binding.txtToTranslate.text.append(copiedText)
            }else{
                Toast.makeText(this, "$copiedText", Toast.LENGTH_SHORT).show()
            }
        }

        languagesFrom = arrayOf(getString(R.string.from),getString(R.string.ar),getString(R.string.en),getString(R.string.af),getString(R.string.be),getString(R.string.bg),getString(R.string.bn),getString(R.string.ca),getString(R.string.cs),getString(R.string.cy),getString(R.string.da),getString(R.string.de),getString(R.string.el),getString(R.string.eo),getString(R.string.es),getString(R.string.et),getString(R.string.fa),getString(R.string.fi),getString(R.string.fr),getString(R.string.ga),getString(R.string.gl),getString(R.string.gu),getString(R.string.he),getString(R.string.hi),getString(R.string.hr),getString(R.string.ht),getString(R.string.hu),getString(R.string.id),getString(R.string.`is`),getString(R.string.it),getString(R.string.ja),getString(R.string.ka),getString(R.string.kn),getString(R.string.ko),getString(R.string.lt),getString(R.string.lv),getString(R.string.mk),getString(R.string.mr),getString(R.string.ms),getString(R.string.mt),getString(R.string.nl),getString(R.string.no),getString(R.string.pl),getString(R.string.pt),getString(R.string.ro),getString(R.string.ru),getString(R.string.sk),getString(R.string.sl),getString(R.string.sq),getString(R.string.sv),getString(R.string.sw),getString(R.string.ta),getString(R.string.te),getString(R.string.th),getString(R.string.tl),getString(R.string.tr),getString(R.string.uk),getString(R.string.ur),getString(R.string.vi),getString(R.string.zh))
        languagesTo = arrayOf(getString(R.string.to),getString(R.string.ar),getString(R.string.en),getString(R.string.af),getString(R.string.be),getString(R.string.bg),getString(R.string.bn),getString(R.string.ca),getString(R.string.cs),getString(R.string.cy),getString(R.string.da),getString(R.string.de),getString(R.string.el),getString(R.string.eo),getString(R.string.es),getString(R.string.et),getString(R.string.fa),getString(R.string.fi),getString(R.string.fr),getString(R.string.ga),getString(R.string.gl),getString(R.string.gu),getString(R.string.he),getString(R.string.hi),getString(R.string.hr),getString(R.string.ht),getString(R.string.hu),getString(R.string.id),getString(R.string.`is`),getString(R.string.it),getString(R.string.ja),getString(R.string.ka),getString(R.string.kn),getString(R.string.ko),getString(R.string.lt),getString(R.string.lv),getString(R.string.mk),getString(R.string.mr),getString(R.string.ms),getString(R.string.mt),getString(R.string.nl),getString(R.string.no),getString(R.string.pl),getString(R.string.pt),getString(R.string.ro),getString(R.string.ru),getString(R.string.sk),getString(R.string.sl),getString(R.string.sq),getString(R.string.sv),getString(R.string.sw),getString(R.string.ta),getString(R.string.te),getString(R.string.th),getString(R.string.tl),getString(R.string.tr),getString(R.string.uk),getString(R.string.ur),getString(R.string.vi),getString(R.string.zh))

        setSupportActionBar(binding.toolbar)
        //binding.toolbar.title = getString(R.string.app_name)
        binding.toolbar.title = getString(R.string.app_name)

        binding.btnSwitchLanguages.setOnClickListener {
            switchLanguages()
        }

        binding.txtChosenLangFrom.setOnClickListener {
            showChooseLanguageDialog(false)
        }

        binding.txtChosenLangTo.setOnClickListener {
            showChooseLanguageDialog(true)
        }


        binding.txtToTranslate.doOnTextChanged { text, _, _, _ ->
            handleTextToTranslateChanged(text)
        }

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        if (text != null) {
                    val languageIdentifier = LanguageIdentification.getClient()
                    languageIdentifier.identifyLanguage(text.toString())
                        .addOnSuccessListener { languageCode ->
                            if (languageCode == "und") {
                                Log.i(TAG, "Can't identify language.")
                                setSourceLanguage(0)
                                setTargetLanguage(0)
                                setTranslateText(text)
                            } else {
                                val langName = getLanguageFromSubname(this,languageCode)

                                if (languagesFrom.contains(langName)){

                                    setSourceLanguage(languagesFrom.indexOf(langName))
                                    val defLang = Locale.getDefault().language
                                    // if text to translate text is mobile default language
                                    // and translate to another language make target is ar if def en || en if def is ar
                                    // else if text to translate is not in phone def language so make target lang is def
                                    val defLangName = if (languageCode == defLang){
                                        if (defLang == "ar"){
                                            getString(R.string.en)
                                        }else{
                                            getString(R.string.ar)
                                        }
                                    }else{
                                        getLanguageFromSubname(this,defLang)
                                    }

                                    if (languagesTo.contains(defLangName)){
                                        setTargetLanguage(languagesFrom.indexOf(defLangName))
                                        setTranslateText(text)
                                    }else{
                                        setSourceLanguage(0)
                                        setTargetLanguage(0)
                                        setTranslateText(text)
                                    }
                                }else{
                                    setSourceLanguage(0)
                                    setTargetLanguage(0)
                                    setTranslateText(text)
                                }
                            }
                        }
                        .addOnFailureListener {
                            setSourceLanguage(0)
                            setTargetLanguage(0)
                            setTranslateText(text)
                        }
        }else{
            setSourceLanguage(savedFromLang)
            setTargetLanguage(savedToLang)
        }

        binding.btnVoiceRecord.setOnClickListener {
         if (fromLanguageCode != defaultLangCode){
             val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
             i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
             i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,getSubnameFromLanguageCode(fromLanguageCode))
             i.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speakToTranslate))

             try {
                 resultType = resultTypeRecordAudio
                 resultLauncher.launch(i)
             }catch (e:java.lang.Exception){
                 Toast.makeText(this, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show()
             }
         }else{
             Toast.makeText(this, getString(R.string.enterSourceLang), Toast.LENGTH_SHORT).show()
         }
        }

    }


    private fun translateText(fromLangCode:Int,toLangCode:Int,textToTranslate:String){
        binding.txtTranslatedText.text = getString(R.string.downloadingModel)
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(fromLangCode)
            .setTargetLanguage(toLangCode)
            .build()


        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        val conditions = FirebaseModelDownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            binding.txtTranslatedText.text = getString(R.string.translating)
            translator.translate(textToTranslate).addOnSuccessListener {
                isTranslatedSuccessfully = true
                binding.txtTranslatedText.text = it
            }.addOnFailureListener {
                isTranslatedSuccessfully = false
                binding.txtTranslatedText.text = getString(R.string.translateSomethingWrong)
            }
        }.addOnFailureListener {
            isTranslatedSuccessfully = false
            binding.txtTranslatedText.text = getString(R.string.SomethingWronginDownload)
        }
    }


    private fun setTranslateText(text: CharSequence?){
        binding.txtToTranslate.setText(text)
    }

    private fun switchLanguages(){
        if (fromLanguageCode!=defaultLangCode&& toLanguageCode!=defaultLangCode){
            setSourceLanguage(lastToLanguagePosition)
            if (!binding.txtToTranslate.text.isNullOrEmpty()){
                binding.txtToTranslate.setText(binding.txtTranslatedText.text.toString())
            }
        } else{
            Toast.makeText(this, getString(R.string.chooseBothLang), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showChooseLanguageDialog(isTargetLanguage:Boolean){
        dialog = Dialog(this)
        val dialogBinding: CustomChooseLanguageDialogBinding = CustomChooseLanguageDialogBinding.inflate(
            LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)


        val adapter = if (isTargetLanguage){
            LanguagesAdapter(this,languagesTo)
        } else {
            LanguagesAdapter(this,languagesFrom)
        }

        dialogBinding.rvLanguages.adapter = adapter
        dialogBinding.rvLanguages.layoutManager = GridLayoutManager(this,3)//2

        adapter.onClickItem = {position: Int ->
            dialog.dismiss()
            if (isTargetLanguage){
                setTargetLanguage(position,true)
            } else {
                setSourceLanguage(position,true)
            }
        }

        dialog.show()
        dialog.window?.setLayout( ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setTargetLanguage(position:Int,withTranslate:Boolean = false){
        val oldPosition =  lastToLanguagePosition
        lastToLanguagePosition = position

        if (position != 0 && position == lastFromLanguagePosition){
            setSourceLanguage(oldPosition)
        }
        lastToLanguagePosition = position
        toLanguageCode = getLanguageCode(this,languagesFrom[position])
        binding.txtChosenLangTo.text = languagesTo[position]

        if (withTranslate){
            if ((!binding.txtToTranslate.text.isNullOrEmpty())&& toLanguageCode != defaultLangCode && fromLanguageCode!=defaultLangCode ){
                translateText(fromLanguageCode,toLanguageCode,binding.txtToTranslate.text.toString())
            }
        }
        savedToLang = position
    }

    private fun setSourceLanguage(position:Int,withTranslate:Boolean = false){
        val oldPosition =  lastFromLanguagePosition
        lastFromLanguagePosition = position
        if (position != 0 && position == lastToLanguagePosition){
            setTargetLanguage(oldPosition)
        }

        fromLanguageCode = getLanguageCode(this,languagesFrom[position])
        binding.txtChosenLangFrom.text = languagesFrom[position]

        if (withTranslate){
            if ((!binding.txtToTranslate.text.isNullOrEmpty())&& toLanguageCode != defaultLangCode && fromLanguageCode!=defaultLangCode ){
                translateText(fromLanguageCode,toLanguageCode,binding.txtToTranslate.text.toString())
            }
        }

        savedFromLang = position
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus){//ضرورية
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                copiedText =  pasteFromClipboard(this)
                if (copiedText.isNullOrEmpty()){
                    binding.btnPaste.visibility = View.GONE
                }else{
                    binding.btnPaste.visibility = View.VISIBLE
                }
            }
        }
    }



    private fun recognizeTextFromImage(resultType:String){
        //don't forget show progress dialog
        val inputImage = if (resultType == resultTypeCamera){
            InputImage.fromBitmap(bitmap,0)
        }else{
            InputImage.fromFilePath(this,imageUri)
        }

        textRecognizerFromImage.process(inputImage).addOnSuccessListener {text->
            binding.txtToTranslate.setText(text.text)
            //handleTextBlock(text)
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.sometingWrong1), Toast.LENGTH_SHORT).show()
        }
    }


/*
    private fun handleTextBlock(result:Text){
        val resultText = result.text
        Log.e(TAG, "handleTextBlock: all $resultText")
        for (block in result.textBlocks) {
            val blockText = block.text
            //val blockConfidence = block.confidence
            //val blockLanguages = block.recognizedLanguages
            val blockLanguages = block.recognizedLanguage
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            Log.e(TAG, "handleTextBlock: Block $blockText ... $blockLanguages ... $blockCornerPoints ... $blockFrame")
            for (line in block.lines) {
                val lineText = line.text
                //val lineConfidence = line.confidence
                //val lineLanguages = line.recognizedLanguages
                val lineLanguages = line.recognizedLanguage
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                Log.e(TAG, "handleTextBlock: Line $lineText ... $lineLanguages ... $lineCornerPoints ... $lineFrame")

                for (element in line.elements) {
                    val elementText = element.text
                    //val elementConfidence = element.confidence
                    //val elementLanguages = element.recognizedLanguages
                    val elementLanguages = element.recognizedLanguage
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                    Log.e(TAG, "handleTextBlock: Element $elementText ... $elementLanguages ... $elementCornerPoints ... $elementFrame")
                }
            }
        }
    }
*/


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorites -> {
                startActivity(Intent(this,FavoritesActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun handleTranslatedTextChanged(text: Editable?){
        if (isTranslatedSuccessfully){
            if (text?.trim().isNullOrEmpty()){
                binding.btnTextToTTS.visibility = View.GONE
                binding.btnCopy.visibility = View.GONE
                binding.btnExpandText.visibility = View.GONE
                binding.btnSetAsFavourite.visibility = View.GONE
            }else{
                binding.btnTextToTTS.visibility = View.VISIBLE
                binding.btnCopy.visibility = View.VISIBLE
                binding.btnExpandText.visibility = View.VISIBLE
                binding.btnSetAsFavourite.visibility = View.VISIBLE
            }
        }
    }


    private fun handleTextToTranslateChanged(text:CharSequence?){
        binding.txtTranslatedText.text = ""
        isTranslatedSuccessfully = false
        if (!binding.txtToTranslate.text.isNullOrEmpty()){
            binding.btnTextFromTTS.visibility = View.VISIBLE
            binding.btnClearText.visibility = View.VISIBLE
            if (fromLanguageCode == defaultLangCode){
                binding.txtTranslatedText.text = getString(R.string.enterSourceLang)
            }else if (toLanguageCode == defaultLangCode){
                binding.txtTranslatedText.text = getString(R.string.enterTargetLang)
            }else{
                translateText(fromLanguageCode,toLanguageCode,text.toString())
                roomViewModel.checkFavoritesIfExists(getSubnameFromLanguageCode(fromLanguageCode),getSubnameFromLanguageCode(toLanguageCode),binding.txtToTranslate.text.toString())
            }
        }else{
            binding.txtTranslatedText.text = ""
            binding.btnTextFromTTS.visibility = View.GONE
            binding.btnClearText.visibility = View.GONE
        }
    }


    private fun initViewModel() {
        roomViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            ),
        )[RoomViewModel::class.java]
    }

    private fun observeGetInsertFavorite() {
        roomViewModel.getInsertFavoriteStatus().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e(TAG, "onCreate:observeGetInsertFavorite SUCCESS ")
                    binding.btnSetAsFavourite.setImageResource(R.drawable.ic_star_filled)
                    binding.btnSetAsFavourite.isEnabled = true
                    isFavorite = true
                }
                Status.LOADING -> {
                    Log.e(TAG, "onCreate:observeGetInsertFavorite LOADING")
                }
                Status.ERROR -> {
                    Log.e(TAG, "onCreate: ERROR to observeGetInsertFavorite")
                }
            }
        }
    }


    private fun observeDeleteFavorite() {
        roomViewModel.getDeleteFavoriteByTextStatus().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e(TAG, "onCreate:observeDeleteFavorite SUCCESS ")
                    binding.btnSetAsFavourite.setImageResource(R.drawable.ic_star_outline_black)
                    binding.btnSetAsFavourite.isEnabled = true
                    isFavorite = false
                }
                Status.LOADING -> {
                    Log.e(TAG, "onCreate:observeDeleteFavorite LOADING")
                }
                Status.ERROR -> {
                    Log.e(TAG, "onCreate: ERROR to observeDeleteFavorite")
                }
            }
        }
    }


    private fun observeCheckFavorite() {
        roomViewModel.checkFavoritesIfExistsResponse().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e(TAG, "onCreate:observeCheckFavorite SUCCESS ")
                    if (!it.data.isNullOrEmpty()){
                        binding.btnSetAsFavourite.setImageResource(R.drawable.ic_star_filled)
                        isFavorite = true
                    }
                }
                Status.LOADING -> {
                    Log.e(TAG, "onCreate:observeCheckFavorite LOADING")
                    binding.btnSetAsFavourite.setImageResource(R.drawable.ic_star_outline_black)
                    isFavorite = false
                }
                Status.ERROR -> {
                    Log.e(TAG, "onCreate: ERROR to observeCheckFavorite")
                }
            }
        }
    }



}