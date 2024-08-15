import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.widget.TextView

fun showAutoDismissDialog(message: String) {
    val builder = AlertDialog.Builder(this)
    val inflater = layoutInflater
    val dialogLayout = inflater.inflate(R.layout.dialog_custom, null)
    val textViewMessage = dialogLayout.findViewById<TextView>(R.id.dialogMessage)

    textViewMessage.text = message

    builder.setView(dialogLayout)
    val dialog = builder.create()

    dialog.show()

    // Fechar o dialog após 3.5 segundos (3500 milissegundos)
    Handler(Looper.getMainLooper()).postDelayed({
        dialog.dismiss()
    }, 3500)
}