package jp.tsumura.takuya.self_maintenance.ForCamera

import android.annotation.TargetApi
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import jp.tsumura.takuya.self_maintenance.R

class Sounds(context: Context){

    private var soundPool:SoundPool? = null

    companion object {

        var SOUND_DRUMROLL = 0
        var SAD_TROMBONE = 0

        var INSTANCE:Sounds? = null
        fun getInstance(context: Context) =
            INSTANCE ?: Sounds(context).also {
                INSTANCE = it
            }
    }
    init {
        createSoundPool()
        loadSoundIDs(context)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createSoundPool() {
        val attributes = AudioAttributes.Builder().apply {
            setUsage(AudioAttributes.USAGE_GAME)
            setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)

        }.build()

        soundPool = SoundPool.Builder().apply {
            setMaxStreams(1)
            setAudioAttributes(attributes)
        }.build()
    }

    private fun loadSoundIDs(context:Context) {
        soundPool?.let {
            println("サウンドファイルロード")
            SOUND_DRUMROLL = it.load(context, R.raw.decision8, 1)
        }
    }

    fun playSound(soundID:Int) {
        soundPool?.let{
            it.play(soundID, 1.0f, 1.0f, 1, 3, 1.0f)
            println("サウンド再生")
        }
    }


    fun close() { // シングルトンの場合呼びようがない？
        soundPool?.release()
        soundPool = null
    }


}