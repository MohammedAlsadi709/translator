package com.alsadimoh.newsolutionstranslate.common

import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alsadimoh.newsolutionstranslate.R
import com.alsadimoh.newsolutionstranslate.databinding.CustomExpandTranslatedTextDialogBinding
import com.alsadimoh.newsolutionstranslate.databinding.CustomProgressBinding
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import java.util.*

object Common {

    const val TAG = "moh"
    private const val isFirstTimeOpenAppKey = "isFirstTimeOpenAppKey"
    private const val savedToLanguageKey = "savedToLanguageKey"
    private const val savedFromLanguageKey = "savedFromLanguageKey"
    const val sharedPrefKey = "sharedPrefKey"
    const val resultTypeRecordAudio = "resultTypeRecordAudio"
    const val resultTypeGallery = "resultTypeGallery"
    const val resultTypeCamera = "resultTypeCamera"
    var clipboardManager: ClipboardManager? = null
    lateinit var myShared: SharedPreferences
    private var alertBuilder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null
    var textToSpeech: TextToSpeech? = null


    var savedToLang: Int
        get():Int {
            return myShared.getInt(savedToLanguageKey, 0)
        }
        set(value) {
            myShared.edit().putInt(savedToLanguageKey, value).apply()
        }

    var savedFromLang: Int
        get():Int {
            return myShared.getInt(savedFromLanguageKey, 0)
        }
        set(value) {
            myShared.edit().putInt(savedFromLanguageKey, value).apply()
        }

    var isFirstTimeOpenApp: Boolean
        get():Boolean {
            return myShared.getBoolean(isFirstTimeOpenAppKey, true)
        }
        set(value) {
            myShared.edit().putBoolean(isFirstTimeOpenAppKey, value).apply()
        }


    fun getLanguageCode(context: Context, lang: String): Int {
        return when (lang) {
            context.getString(R.string.ar) -> FirebaseTranslateLanguage.AR
            context.getString(R.string.en) -> FirebaseTranslateLanguage.EN
            context.getString(R.string.af) -> FirebaseTranslateLanguage.AF
            context.getString(R.string.be) -> FirebaseTranslateLanguage.BE
            context.getString(R.string.bg) -> FirebaseTranslateLanguage.BG
            context.getString(R.string.bn) -> FirebaseTranslateLanguage.BN
            context.getString(R.string.ca) -> FirebaseTranslateLanguage.CA
            context.getString(R.string.cs) -> FirebaseTranslateLanguage.CS
            context.getString(R.string.cy) -> FirebaseTranslateLanguage.CY
            context.getString(R.string.da) -> FirebaseTranslateLanguage.DA
            context.getString(R.string.de) -> FirebaseTranslateLanguage.DE
            context.getString(R.string.el) -> FirebaseTranslateLanguage.EL
            context.getString(R.string.eo) -> FirebaseTranslateLanguage.EO
            context.getString(R.string.es) -> FirebaseTranslateLanguage.ES
            context.getString(R.string.et) -> FirebaseTranslateLanguage.ET
            context.getString(R.string.fa) -> FirebaseTranslateLanguage.FA
            context.getString(R.string.fi) -> FirebaseTranslateLanguage.FI
            context.getString(R.string.fr) -> FirebaseTranslateLanguage.FR
            context.getString(R.string.ga) -> FirebaseTranslateLanguage.GA
            context.getString(R.string.gl) -> FirebaseTranslateLanguage.GL
            context.getString(R.string.gu) -> FirebaseTranslateLanguage.GU
            context.getString(R.string.he) -> FirebaseTranslateLanguage.HE
            context.getString(R.string.hi) -> FirebaseTranslateLanguage.HI
            context.getString(R.string.hr) -> FirebaseTranslateLanguage.HR
            context.getString(R.string.ht) -> FirebaseTranslateLanguage.HT
            context.getString(R.string.hu) -> FirebaseTranslateLanguage.HU
            context.getString(R.string.id) -> FirebaseTranslateLanguage.ID
            context.getString(R.string.`is`) -> FirebaseTranslateLanguage.IS
            context.getString(R.string.it) -> FirebaseTranslateLanguage.IT
            context.getString(R.string.ja) -> FirebaseTranslateLanguage.JA
            context.getString(R.string.ka) -> FirebaseTranslateLanguage.KA
            context.getString(R.string.kn) -> FirebaseTranslateLanguage.KN
            context.getString(R.string.ko) -> FirebaseTranslateLanguage.KO
            context.getString(R.string.lt) -> FirebaseTranslateLanguage.LT
            context.getString(R.string.lv) -> FirebaseTranslateLanguage.LV
            context.getString(R.string.mk) -> FirebaseTranslateLanguage.MK
            context.getString(R.string.mr) -> FirebaseTranslateLanguage.MR
            context.getString(R.string.ms) -> FirebaseTranslateLanguage.MS
            context.getString(R.string.mt) -> FirebaseTranslateLanguage.MT
            context.getString(R.string.nl) -> FirebaseTranslateLanguage.NL
            context.getString(R.string.no) -> FirebaseTranslateLanguage.NO
            context.getString(R.string.pl) -> FirebaseTranslateLanguage.PL
            context.getString(R.string.pt) -> FirebaseTranslateLanguage.PT
            context.getString(R.string.ro) -> FirebaseTranslateLanguage.RO
            context.getString(R.string.ru) -> FirebaseTranslateLanguage.RU
            context.getString(R.string.sk) -> FirebaseTranslateLanguage.SK
            context.getString(R.string.sl) -> FirebaseTranslateLanguage.SL
            context.getString(R.string.sq) -> FirebaseTranslateLanguage.SQ
            context.getString(R.string.sv) -> FirebaseTranslateLanguage.SV
            context.getString(R.string.sw) -> FirebaseTranslateLanguage.SW
            context.getString(R.string.ta) -> FirebaseTranslateLanguage.TA
            context.getString(R.string.te) -> FirebaseTranslateLanguage.TE
            context.getString(R.string.th) -> FirebaseTranslateLanguage.TH
            context.getString(R.string.tl) -> FirebaseTranslateLanguage.TL
            context.getString(R.string.tr) -> FirebaseTranslateLanguage.TR
            context.getString(R.string.uk) -> FirebaseTranslateLanguage.UK
            context.getString(R.string.ur) -> FirebaseTranslateLanguage.UR
            context.getString(R.string.vi) -> FirebaseTranslateLanguage.VI
            context.getString(R.string.zh) -> FirebaseTranslateLanguage.ZH
            else -> -1
        }
    }


    fun getNameFromLanguageCode(context: Context, langCode: Int): String {
        return when (langCode) {
            FirebaseTranslateLanguage.AR -> context.getString(R.string.ar)
            FirebaseTranslateLanguage.EN -> context.getString(R.string.en)
            FirebaseTranslateLanguage.AF -> context.getString(R.string.af)
            FirebaseTranslateLanguage.BE -> context.getString(R.string.be)
            FirebaseTranslateLanguage.BG -> context.getString(R.string.bg)
            FirebaseTranslateLanguage.BN -> context.getString(R.string.bn)
            FirebaseTranslateLanguage.CA -> context.getString(R.string.ca)
            FirebaseTranslateLanguage.CS -> context.getString(R.string.cs)
            FirebaseTranslateLanguage.CY -> context.getString(R.string.cy)
            FirebaseTranslateLanguage.DA -> context.getString(R.string.da)
            FirebaseTranslateLanguage.DE -> context.getString(R.string.de)
            FirebaseTranslateLanguage.EL -> context.getString(R.string.el)
            FirebaseTranslateLanguage.EO -> context.getString(R.string.eo)
            FirebaseTranslateLanguage.ES -> context.getString(R.string.es)
            FirebaseTranslateLanguage.ET -> context.getString(R.string.et)
            FirebaseTranslateLanguage.FA -> context.getString(R.string.fa)
            FirebaseTranslateLanguage.FI -> context.getString(R.string.fi)
            FirebaseTranslateLanguage.FR -> context.getString(R.string.fr)
            FirebaseTranslateLanguage.GA -> context.getString(R.string.ga)
            FirebaseTranslateLanguage.GL -> context.getString(R.string.gl)
            FirebaseTranslateLanguage.GU -> context.getString(R.string.gu)
            FirebaseTranslateLanguage.HE -> context.getString(R.string.he)
            FirebaseTranslateLanguage.HI -> context.getString(R.string.hi)
            FirebaseTranslateLanguage.HR -> context.getString(R.string.hr)
            FirebaseTranslateLanguage.HT -> context.getString(R.string.ht)
            FirebaseTranslateLanguage.HU -> context.getString(R.string.hu)
            FirebaseTranslateLanguage.ID -> context.getString(R.string.id)
            FirebaseTranslateLanguage.IS -> context.getString(R.string.`is`)
            FirebaseTranslateLanguage.IT -> context.getString(R.string.it)
            FirebaseTranslateLanguage.JA -> context.getString(R.string.ja)
            FirebaseTranslateLanguage.KA -> context.getString(R.string.ka)
            FirebaseTranslateLanguage.KN -> context.getString(R.string.kn)
            FirebaseTranslateLanguage.KO -> context.getString(R.string.ko)
            FirebaseTranslateLanguage.LT -> context.getString(R.string.lt)
            FirebaseTranslateLanguage.LV -> context.getString(R.string.lv)
            FirebaseTranslateLanguage.MK -> context.getString(R.string.mk)
            FirebaseTranslateLanguage.MR -> context.getString(R.string.mr)
            FirebaseTranslateLanguage.MS -> context.getString(R.string.ms)
            FirebaseTranslateLanguage.MT -> context.getString(R.string.mt)
            FirebaseTranslateLanguage.NL -> context.getString(R.string.nl)
            FirebaseTranslateLanguage.NO -> context.getString(R.string.no)
            FirebaseTranslateLanguage.PL -> context.getString(R.string.pl)
            FirebaseTranslateLanguage.PT -> context.getString(R.string.pt)
            FirebaseTranslateLanguage.RO -> context.getString(R.string.ro)
            FirebaseTranslateLanguage.RU -> context.getString(R.string.ru)
            FirebaseTranslateLanguage.SK -> context.getString(R.string.sk)
            FirebaseTranslateLanguage.SL -> context.getString(R.string.sl)
            FirebaseTranslateLanguage.SQ -> context.getString(R.string.sq)
            FirebaseTranslateLanguage.SV -> context.getString(R.string.sv)
            FirebaseTranslateLanguage.SW -> context.getString(R.string.sw)
            FirebaseTranslateLanguage.TA -> context.getString(R.string.ta)
            FirebaseTranslateLanguage.TE -> context.getString(R.string.te)
            FirebaseTranslateLanguage.TH -> context.getString(R.string.th)
            FirebaseTranslateLanguage.TL -> context.getString(R.string.tl)
            FirebaseTranslateLanguage.TR -> context.getString(R.string.tr)
            FirebaseTranslateLanguage.UK -> context.getString(R.string.uk)
            FirebaseTranslateLanguage.UR -> context.getString(R.string.ur)
            FirebaseTranslateLanguage.VI -> context.getString(R.string.vi)
            FirebaseTranslateLanguage.ZH -> context.getString(R.string.zh)
            else -> context.getString(R.string.from)
        }
    }


    fun getSubnameFromLanguageCode(langCode: Int): String {
        return when (langCode) {
            FirebaseTranslateLanguage.AR -> "ar"
            FirebaseTranslateLanguage.EN -> "en"
            FirebaseTranslateLanguage.AF -> "af"
            FirebaseTranslateLanguage.BE -> "be"
            FirebaseTranslateLanguage.BG -> "bg"
            FirebaseTranslateLanguage.BN -> "bn"
            FirebaseTranslateLanguage.CA -> "ca"
            FirebaseTranslateLanguage.CS -> "cs"
            FirebaseTranslateLanguage.CY -> "cy"
            FirebaseTranslateLanguage.DA -> "da"
            FirebaseTranslateLanguage.DE -> "de"
            FirebaseTranslateLanguage.EL -> "el"
            FirebaseTranslateLanguage.EO -> "eo"
            FirebaseTranslateLanguage.ES -> "es"
            FirebaseTranslateLanguage.ET -> "et"
            FirebaseTranslateLanguage.FA -> "fa"
            FirebaseTranslateLanguage.FI -> "fi"
            FirebaseTranslateLanguage.FR -> "fr"
            FirebaseTranslateLanguage.GA -> "ga"
            FirebaseTranslateLanguage.GL -> "gl"
            FirebaseTranslateLanguage.GU -> "gu"
            FirebaseTranslateLanguage.HE -> "he"
            FirebaseTranslateLanguage.HI -> "hi"
            FirebaseTranslateLanguage.HR -> "hr"
            FirebaseTranslateLanguage.HT -> "ht"
            FirebaseTranslateLanguage.HU -> "hu"
            FirebaseTranslateLanguage.ID -> "id"
            FirebaseTranslateLanguage.IS -> "is"
            FirebaseTranslateLanguage.IT -> "it"
            FirebaseTranslateLanguage.JA -> "ja"
            FirebaseTranslateLanguage.KA -> "ka"
            FirebaseTranslateLanguage.KN -> "kn"
            FirebaseTranslateLanguage.KO -> "ko"
            FirebaseTranslateLanguage.LT -> "lt"
            FirebaseTranslateLanguage.LV -> "lv"
            FirebaseTranslateLanguage.MK -> "mk"
            FirebaseTranslateLanguage.MR -> "mr"
            FirebaseTranslateLanguage.MS -> "ms"
            FirebaseTranslateLanguage.MT -> "mt"
            FirebaseTranslateLanguage.NL -> "nl"
            FirebaseTranslateLanguage.NO -> "no"
            FirebaseTranslateLanguage.PL -> "pl"
            FirebaseTranslateLanguage.PT -> "pt"
            FirebaseTranslateLanguage.RO -> "ro"
            FirebaseTranslateLanguage.RU -> "ru"
            FirebaseTranslateLanguage.SK -> "sk"
            FirebaseTranslateLanguage.SL -> "sl"
            FirebaseTranslateLanguage.SQ -> "sq"
            FirebaseTranslateLanguage.SV -> "sv"
            FirebaseTranslateLanguage.SW -> "sw"
            FirebaseTranslateLanguage.TA -> "ta"
            FirebaseTranslateLanguage.TE -> "te"
            FirebaseTranslateLanguage.TH -> "th"
            FirebaseTranslateLanguage.TL -> "tl"
            FirebaseTranslateLanguage.TR -> "tr"
            FirebaseTranslateLanguage.UK -> "uk"
            FirebaseTranslateLanguage.UR -> "ur"
            FirebaseTranslateLanguage.VI -> "vi"
            FirebaseTranslateLanguage.ZH -> "zh"
            else -> Locale.getDefault().language
        }
    }


    fun getLanguageFromSubname(context: Context, lang: String): String {
        return when (lang) {
            "en" -> context.getString(R.string.en)
            "ar" -> context.getString(R.string.ar)
            "af" -> context.getString(R.string.af)
            "be" -> context.getString(R.string.be)
            "bg" -> context.getString(R.string.bg)
            "bn" -> context.getString(R.string.bn)
            "ca" -> context.getString(R.string.ca)
            "cs" -> context.getString(R.string.cs)
            "da" -> context.getString(R.string.da)
            "el" -> context.getString(R.string.el)
            "eo" -> context.getString(R.string.eo)
            "es" -> context.getString(R.string.es)
            "et" -> context.getString(R.string.et)
            "fa" -> context.getString(R.string.fa)
            "fi" -> context.getString(R.string.fi)
            "fr" -> context.getString(R.string.fr)
            "ga" -> context.getString(R.string.ga)
            "gl" -> context.getString(R.string.gl)
            "gu" -> context.getString(R.string.gu)
            "he" -> context.getString(R.string.he)
            "hi" -> context.getString(R.string.hi)
            "hr" -> context.getString(R.string.hr)
            "ht" -> context.getString(R.string.ht)
            "id" -> context.getString(R.string.id)
            "is" -> context.getString(R.string.`is`)
            "it" -> context.getString(R.string.it)
            "ja" -> context.getString(R.string.ja)
            "ka" -> context.getString(R.string.ka)
            "kn" -> context.getString(R.string.kn)
            "ko" -> context.getString(R.string.ko)
            "lt" -> context.getString(R.string.lt)
            "lv" -> context.getString(R.string.lv)
            "mk" -> context.getString(R.string.mk)
            "mr" -> context.getString(R.string.mr)
            "ms" -> context.getString(R.string.ms)
            "mt" -> context.getString(R.string.mt)
            "nl" -> context.getString(R.string.nl)
            "no" -> context.getString(R.string.no)
            "pl" -> context.getString(R.string.pl)
            "pt" -> context.getString(R.string.pt)
            "ro" -> context.getString(R.string.ro)
            "ru" -> context.getString(R.string.ru)
            "sk" -> context.getString(R.string.sk)
            "sl" -> context.getString(R.string.sl)
            "sq" -> context.getString(R.string.sq)
            "sv" -> context.getString(R.string.sv)
            "sw" -> context.getString(R.string.sw)
            "ta" -> context.getString(R.string.ta)
            "te" -> context.getString(R.string.te)
            "tl" -> context.getString(R.string.tl)
            "th" -> context.getString(R.string.th)
            "tr" -> context.getString(R.string.tr)
            "uk" -> context.getString(R.string.uk)
            "ur" -> context.getString(R.string.ur)
            "vi" -> context.getString(R.string.vi)
            "zh" -> context.getString(R.string.zh)
            else -> context.getString(R.string.from)
        }
    }


    fun checkReadStoragePermissions(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraPermissions(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun showExpandCardDialog(context: Context,textTranslated:String,sourceText:String = "",targetLang:String = "",sourceLang:String = "",isFromMain:Boolean = true){
        val dialog = Dialog(context)
        val dialogBinding = CustomExpandTranslatedTextDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)
        if (isFromMain){
            dialogBinding.btnLang.visibility = View.GONE
            dialogBinding.btnTTS.visibility = View.GONE
        }else{
            dialogBinding.btnTTS.visibility = View.VISIBLE
            dialogBinding.btnLang.visibility = View.VISIBLE
            dialogBinding.btnLang.text = targetLang
            dialogBinding.btnLang.setOnClickListener {
                if (dialogBinding.btnLang.text.equals(targetLang)){
                    dialogBinding.btnLang.text = sourceLang
                    dialogBinding.dialogText.text= sourceText
                }else{
                    dialogBinding.btnLang.text = targetLang
                    dialogBinding.dialogText.text= textTranslated
                }
            }

            dialogBinding.btnTTS.setOnClickListener {
                if (dialogBinding.btnLang.text.equals(targetLang) ){
                    speakTextTTS(context,textTranslated, targetLang,dialogBinding)
                }else{
                    speakTextTTS(context,sourceText, sourceLang,dialogBinding)
                }
            }
        }
        dialogBinding.dialogText.text= textTranslated

        dialogBinding.btnCopy.setOnClickListener {
            copyToClipboard(context,dialogBinding.dialogText.text.toString())
        }

        dialogBinding.dialogBtnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout( ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }



    fun speakTextTTS(context: Context,text: String,langSubName: String,dialogBinding: CustomExpandTranslatedTextDialogBinding? = null){
        if (textToSpeech!=null){
            speakText(context,text, langSubName,dialogBinding)
        }else{
            textToSpeech = TextToSpeech(context) {
                speakText(context,text, langSubName,dialogBinding)
            }
        }
    }


    private fun speakText(context: Context,text:String, langSubName:String,dialogBinding: CustomExpandTranslatedTextDialogBinding? = null){
        val result =  textToSpeech!!.setLanguage(Locale(langSubName))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The Language specified is not supported!")
        }else{
             val speechListener = object : UtteranceProgressListener() {
                 override fun onStart(utteranceId: String?) {
                    dialogBinding?.btnTTS?.setColorFilter(ContextCompat.getColor(context, R.color.views), android.graphics.PorterDuff.Mode.SRC_IN)
                 }

                 override fun onDone(utteranceId: String?) {
                     dialogBinding?.btnTTS?.setColorFilter(ContextCompat.getColor(context, R.color.iconsTint), android.graphics.PorterDuff.Mode.SRC_IN)
                 }

                 override fun onError(p0: String?) {
                     dialogBinding?.btnTTS?.setColorFilter(ContextCompat.getColor(context, R.color.iconsTint), android.graphics.PorterDuff.Mode.SRC_IN)
                 }
             }

             //textToSpeech?.stop()
             //textToSpeech?.shutdown()

             textToSpeech!!.setOnUtteranceProgressListener(speechListener)
            textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }




    fun pasteFromClipboard(context: Context):CharSequence?{
        if (clipboardManager == null){
            clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }
        return clipboardManager?.primaryClip?.getItemAt(0)?.text
    }

    fun copyToClipboard(context: Context,text:String){
        if (clipboardManager == null){
            clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }
        clipboardManager?.setPrimaryClip(ClipData.newPlainText("text",text))
        try {
            Toast.makeText(context, context.getString(R.string.Copied), Toast.LENGTH_SHORT).show()
        }catch (_:java.lang.Exception){}
    }


    fun showProgressDialog(context: Context) {

        alertBuilder = AlertDialog.Builder(context)
        val binding = CustomProgressBinding.inflate(LayoutInflater.from(context))
        alertBuilder!!.setView(binding.root)

        alertBuilder!!.setCancelable(false)
        dialog = alertBuilder!!.create()

        dialog!!.show()
    }

    fun hideProgressDialog() {
        if (dialog!= null){
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }



}