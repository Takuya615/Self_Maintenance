package jp.tsumura.takuya.self_maintenance.ForCamera
/*
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.dialog_camera.view.*

class CameraDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_camera, null, false)

        val dateList = arrayListOf<String>("1","1","1","1","1","1","1","1","1","1","1","1","1")
        view.cameraDialogRecyclerView.layoutManager = GridLayoutManager(activity,7)
        view.cameraDialogRecyclerView.adapter = CameraDialogAdapter(dateList)

        builder.setView(view)
        return builder.create()
    }

}

 */