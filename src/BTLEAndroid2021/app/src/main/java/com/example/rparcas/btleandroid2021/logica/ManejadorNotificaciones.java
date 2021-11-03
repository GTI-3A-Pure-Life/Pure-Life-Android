package com.example.rparcas.btleandroid2021.logica;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.rparcas.btleandroid2021.R;
import androidx.core.app.NotificationCompat;

/**
 * ManejadorNotificaciones.java
 * @author Ruben Pardo Casanova
 * 25/10/2021
 * Clase para gestionar las notificaciones, crearlas y lanzarlas
 */
public class ManejadorNotificaciones extends ContextWrapper {


    private NotificationManager manejadorNotificaciones;
    private final int icono_notificacion;
    private final String id_canal;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    /**
     * Constructor del manejador de notificaciones
     *
     * Texto, Texto, Texto, N, N, Context -> ManejadorNotificaciones()
     *
     * @param canal_id id del canal que crea si tenemos una version superior a api 26
     * @param nombre_canal nombre del canal que creamos
     * @param descripcion_canal descripcion del canal
     * @param importancia importancia del canal (NotificationManager.IMPORTANCE_DEFAULT, etc)
     * @param icono_notificacion icono que se muestra en la parte superior cuando se lanza la notificacion (==R.drawable...)
     * @param context contexto de la aplicacion
     */
    public ManejadorNotificaciones(String canal_id, String nombre_canal, String descripcion_canal,
                                   int importancia, int icono_notificacion,Context context) {

        super(context);
        Log.d("PRUEBA", "ManejadorNotificaciones: context "+context);
        Log.d("PRUEBA", "ManejadorNotificaciones: this "+this);
        Log.d("PRUEBA", "ManejadorNotificaciones: base context "+getBaseContext());
        manejadorNotificaciones = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        this.icono_notificacion = icono_notificacion;
        this.id_canal = canal_id;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    this.id_canal,
                    nombre_canal,
                    importancia);

            notificationChannel.setDescription(descripcion_canal);
            manejadorNotificaciones.createNotificationChannel(notificationChannel);
        }

    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     * Texto, Texto, Texto, Accion -> crearNotificacion() -> Notificacion
     *
     * Creamos una notificacion pero no la lanzamos desde aqui por si se quiere añadir funcionalidad
     * como por ejemplo intents y demas
     *
     * @param titulo el titulo de la notificacion
     * @param contenido el cuerpo de la notificacion
     * @param intencionPendiente el pendingIntent que apunta a la actividad que se lanzara al pulsar la actividad (null para no añadir ninguna)
     * @return el builder de la notificacion que contiene la notificacion (notificacion.notify(canal_id,builder))
     */
    public NotificationCompat.Builder crearNotificacion(String titulo, String contenido,
                                                        PendingIntent intencionPendiente) {

        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(getApplicationContext(), this.id_canal)
                    .setSmallIcon(icono_notificacion)
                    .setContentTitle(titulo)
                    .setContentText(contenido)
                    .setAutoCancel(true);


        // accion
        if(intencionPendiente!=null){
            notificacion.setContentIntent(intencionPendiente);
        }

        return notificacion;

    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     *
     * Texto, Texto, Texto, int, int, Accion -> crearNotificacion() -> Notificacion
     *
     * Crear una notificacion con un layout personalizado con
     * titulo (id = notificacion_pequenya_titulo),
     * contenido (id = notificacion_pequenya_contenido),
     * imagen (id = notificacion_pequenya_imagen)
     *
     * Son los id del recurso xml
     *
     * @param titulo el titulo de la notificacion
     * @param contenido el cuerpo de la notificacion
     * @param layout_personalizado referencia al layout de como se vera la notificacion (==R.layout.mi_layaout)
     * @param imagen_icono referencia al icono que se representara en el layout (== R.minmap.icono)
     * @param intencionPendiente accion que realizara al pulsar la notificacion
     * @return el builder de la notificacion que contiene la notificacion (notificacion.notify(canal_id,builder))
     */
    public NotificationCompat.Builder crearNotificacionPersonalizada(String titulo, String contenido,
                                                        int layout_personalizado, int imagen_icono,
                                                        PendingIntent intencionPendiente) {



        // layout personalizado
        RemoteViews contentView = new RemoteViews(getPackageName(), layout_personalizado);
        contentView.setImageViewResource(R.id.notificacion_pequenya_imagen, imagen_icono);
        contentView.setTextViewText(R.id.notificacion_pequenya_titulo, titulo);
        contentView.setTextViewText(R.id.notificacion_pequenya_contenido, contenido);

        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(getApplicationContext(), this.id_canal)
                    .setSmallIcon(icono_notificacion)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(contentView)
                    .setAutoCancel(true);



        // accion
        if(intencionPendiente!=null){
            notificacion.setContentIntent(intencionPendiente);
        }

        return notificacion;

    }



    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     *
     * N, Notificacion -> lanzarNotificacion()
     *
     * Lanzar la notificacion
     *
     * @param id la ID de la notificacion
     * @param notificacion El objeto notificacion
     */
    public void lanzarNotificacion(int id, NotificationCompat.Builder notificacion) {
        obtenerManejador().notify(id, notificacion.build());
    }



    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     *
     * obtenerManejador() -> NotificationManager
     * @return El sistema de serivcio NotificationManager
     */
    private NotificationManager obtenerManejador() {
        if (manejadorNotificaciones == null) {
            manejadorNotificaciones = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manejadorNotificaciones;
    } // ()




}
