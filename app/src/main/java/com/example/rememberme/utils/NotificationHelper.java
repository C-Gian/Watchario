package com.example.rememberme.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rememberme.MainElement;
import com.example.rememberme.MainActivity;
import com.example.rememberme.MainFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public final class NotificationHelper {
    private final String CHANNEL_ID = "reminder_channel_id";
    private int NOTIFICATION_ID = 1;
    private final Context context;
    String URL = "https://rememberapp-13f74-default-rtdb.europe-west1.firebasedatabase.app/";
    String URL_storage = "gs://rememberapp-13f74.appspot.com";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance(URL).getReference();
    long today_date = 0;
    StorageReference storageReference, storageReference2;
    MainElement element;
    Bitmap bitmap;
    Notification notification;

    public NotificationHelper(@NotNull Context context) {
        this.context = context;
    }

    public void createNotification(@NotNull String name, @NotNull String type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today_date = Instant.now().getEpochSecond();
        }
        //retrieve the element
        databaseReference = databaseReference.child("Main").child(type).child("ToRelease");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(name)) {
                        element = ds.getValue(MainElement.class);
                        element.setName(ds.getKey());
                        //if here then the element has not been trasposed in the right new section when its date came out
                        storageReference = FirebaseStorage.getInstance(URL_storage).getReferenceFromUrl(element.getImage_url());
                        storageReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                System.out.println("Element created and uploaded with success");
                                bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes .length);
                                storageReference = FirebaseStorage.getInstance(URL_storage).getReference("Images/" + "Main/" + (element.getType()+"/") + "Released/" + element.getName());
                                storageReference2 = FirebaseStorage.getInstance(URL_storage).getReference("Images/" + "Notifications/" + element.getType() + "/" + element.getName());
                                storageReference.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        System.out.println("Element created and uploaded with success");
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                System.out.println("Element created and uploaded with success");
                                                storageReference = FirebaseStorage.getInstance(URL_storage).getReferenceFromUrl(element.getImage_url());
                                                databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Main");
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        System.out.println("Element images removed successfully from firebase");
                                                        databaseReference.child(element.getType()).child(element.getTime()).child(element.getName()).removeValue();
                                                        databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Main").child(element.getType()).child("Released");
                                                        databaseReference.child(element.getName()).child("description").setValue(element.getDescription());
                                                        databaseReference.child(element.getName()).child("type").setValue(element.getType());
                                                        databaseReference.child(element.getName()).child("time").setValue("Released");
                                                        databaseReference.child(element.getName()).child("important_to_watch").setValue(element.isImportant_to_watch());
                                                        databaseReference.child(element.getName()).child("future_release_date").setValue("null");
                                                        databaseReference.child(element.getName()).child("image_url").setValue(uri.toString());
                                                        databaseReference.child(element.getName()).child("done").setValue(false);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            databaseReference.child(element.getName()).child("element_date").setValue(Instant.now().getEpochSecond());
                                                        }
                                                        databaseReference = FirebaseDatabase.getInstance(URL).getReference().child("Extra").child("Notifications");
                                                        databaseReference.child(element.getName()).child("image_url").setValue(uri.toString());
                                                        databaseReference.child(element.getName()).child("desc").setValue(element.getDescription());
                                                        databaseReference = FirebaseDatabase.getInstance(URL).getReference();
                                                        if (Build.VERSION.SDK_INT >= 26) {
                                                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                                                            channel.setDescription("Reminder Channel Description");
                                                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                            notificationManager.createNotificationChannel(channel);
                                                        }
                                                        Intent intent = new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                                                        notification = null;
                                                        String name, description;
                                                        if (element.getName().length() > 20) {
                                                            name = element.getName().substring(0, 20) + "... ";
                                                            description = element.getName().substring(0, 20) + "... ";
                                                        }else {
                                                            name = element.getName();
                                                            description = element.getName();
                                                        }
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                                                                    .setSmallIcon(getContext().getApplicationInfo().icon)
                                                                    .setLargeIcon(bitmap)
                                                                    .setContentTitle(name + " Came Out!")
                                                                    .setContentText(description + " Reached its Release Date")
                                                                    .setContentIntent(pendingIntent)
                                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                    .build();
                                                        }
                                                        NOTIFICATION_ID = MainFragment.notifications_number;
                                                        MainFragment.notifications_number += 1;
                                                        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        System.out.println("ERROR: Element images remove fail from firebase");
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                System.out.println("ERROR: element not created");
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NotNull
    public final Context getContext() {
        return this.context;
    }


}
