package victor96man.mitiempo

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity


class Network {

    companion object {
        /**
         * Función para comprobar conexión
         * @param activity
         * @see connectivityManager: Administra temas de red
         * @see networkInfo: almacena el estado de la red.
         * @return devuelve un boolean que verifica si hay o no red disponible.
         */
        fun hayRed(activity: AppCompatActivity): Boolean {

            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            return networkInfo != null && networkInfo.isConnected

        }
    }
}