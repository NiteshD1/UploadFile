package com.example.uploadfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Jay Shree RAM";

    // preparing key for firestore

    private static final String KEY_NAME = "Name";
    private static final String KEY_LINK = "Link";
    private static final String KEY_Q_STARTWITH = "QuestionStartWith";
    private static final String KEY_O_STARTWITH = "OptionStartWith";
    private static final String KEY_O2_STARTWITH = "Option2StartWith";
    private static final String KEY_O3_STARTWITH = "Option3StartWith";
    private static final String KEY_O4_STARTWITH = "Option4StartWith";
    private static final String KEY_A_STARTWITH = "AnswerStartWith";
    private static final String KEY_S_STARTWITH = "SolutionStartWith";
    private static final String KEY_MARKS = "Marks";
    private static final String KEY_NEGATIVE_MARKS = "MinusMarks";
    private static final String KEY_TOTAL_QUESTION = "TotalQuestion";
    private static final String KEY_TOTAL_TIME = "TotalTime";
    private static final String KEY_AS_INCLUDED = "AnswerOrSolutionIncluded";



    // firestore object

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // autocompleteView

    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteTextView autoCompleteExamName;
    private AutoCompleteTextView autoCompletePorS;
    private AutoCompleteTextView autoCompleteqstartWith;
    private AutoCompleteTextView autoCompleteostartWith;
    private AutoCompleteTextView autoCompleteo2startWith;
    private AutoCompleteTextView autoCompleteo3startWith;
    private AutoCompleteTextView autoCompleteo4startWith;
    private AutoCompleteTextView autoCompleteastartWith;
    private AutoCompleteTextView autoCompletesstartWith;
    private AutoCompleteTextView autoCompletemarks;
    private AutoCompleteTextView autoCompletenegativemarks;
    private AutoCompleteTextView autoCompletetotalquestion;
    private AutoCompleteTextView autoCompletetotaltime;
    private AutoCompleteTextView autoCompleteanswerIncluded;
    private AutoCompleteTextView autoCompletesolutionIncluded;

    // string of autocomplete text view

    private String answerOrSolutionIncluded = null;
    private String autoFileName = null;
    private String autoExamName = null;
    private String autoPorS = null;
    private String qstartWith = null;
    private String ostartWith = null;
    private String o2startWith = null;
    private String o3startWith = null;
    private String o4startWith = null;
    private String astartWith = null;
    private String sstartWith = null;
    private String marks = null;
    private String negativemarks = null;
    private String totalQuestion = null;
    private String totalTime = null;
    private String answerIncluded = null;
    private String solutionIncluded = null;

    // autoCompleteView words array

    private static String[] uploadHelpWords = new String[]{
            "Papers", "Jee Main", "Solutions", "Jee Advanced", "Upsc",
            "Ssc", "Previous Year Paper", "Sbi Po", "Ibps Po", "Ibps so" ,"Answers" ,"Yes", "No"
    };

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    //Buttons
    private Button buttonChoose;
    private Button buttonUpload;

    //ImageView
    private ImageView imageView;

    //a Uri object to store file path
    private Uri filePath;

    // creating storage reference

    private StorageReference storageReference;

    // firebase storage url

    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assigning autocompleteText view

        autoCompleteTextView = findViewById(R.id.actv);
        autoCompleteExamName = findViewById(R.id.actvExam);
        autoCompletePorS = findViewById(R.id.actvPorS);
        autoCompleteqstartWith = findViewById(R.id.q_startwith);
        autoCompleteostartWith = findViewById(R.id.o_startwith);
        autoCompleteo2startWith = findViewById(R.id.o2_startwith);
        autoCompleteo3startWith = findViewById(R.id.o3_startwith);
        autoCompleteo4startWith = findViewById(R.id.o4_startwith);
        autoCompleteastartWith = findViewById(R.id.a_startwith);
        autoCompletesstartWith = findViewById(R.id.s_startwith);
        autoCompletemarks = findViewById(R.id.marks);
        autoCompletenegativemarks = findViewById(R.id.negative_marks);
        autoCompletetotalquestion = findViewById(R.id.total_question);
        autoCompletetotaltime = findViewById(R.id.total_time);
        autoCompleteanswerIncluded = findViewById(R.id.answer_included);
        autoCompletesolutionIncluded = findViewById(R.id.solution_included);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,uploadHelpWords);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteExamName.setAdapter(adapter);
        autoCompletePorS.setAdapter(adapter);
        autoCompleteqstartWith.setAdapter(adapter);
        autoCompleteostartWith.setAdapter(adapter);
        autoCompleteo2startWith.setAdapter(adapter);
        autoCompleteo3startWith.setAdapter(adapter);
        autoCompleteo4startWith.setAdapter(adapter);
        autoCompleteastartWith.setAdapter(adapter);
        autoCompletesstartWith.setAdapter(adapter);
        autoCompletemarks.setAdapter(adapter);
        autoCompletenegativemarks.setAdapter(adapter);
        autoCompletetotalquestion.setAdapter(adapter);
        autoCompletetotaltime.setAdapter(adapter);
        autoCompleteanswerIncluded.setAdapter(adapter);
        autoCompletesolutionIncluded.setAdapter(adapter);



        // assingning storageReference

        storageReference = FirebaseStorage.getInstance().getReference();

        //getting views from layout
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

//        imageView = (ImageView) findViewById(R.id.imageView);

        //attaching listener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            Toast.makeText(getApplicationContext(),"file chosen",Toast.LENGTH_SHORT).show();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == buttonChoose) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == buttonUpload) {

            //Log.i(TAG,autoCompleteExamName.getText().toString());

            uploadFile();
        }
    }

    //this method will upload the file
    private void uploadFile() {

        // get name from autoComplete text View

        autoFileName = autoCompleteTextView.getText().toString();
        autoExamName = autoCompleteExamName.getText().toString();
        autoPorS = autoCompletePorS.getText().toString();
        qstartWith = autoCompleteqstartWith.getText().toString();
        ostartWith = autoCompleteostartWith.getText().toString();
        o2startWith = autoCompleteo2startWith.getText().toString();
        o3startWith = autoCompleteo3startWith.getText().toString();
        o4startWith = autoCompleteo4startWith.getText().toString();
        astartWith = autoCompleteastartWith.getText().toString();
        sstartWith = autoCompletesstartWith.getText().toString();
        marks = autoCompletemarks.getText().toString();
        negativemarks = autoCompletenegativemarks.getText().toString();
        totalQuestion = autoCompletetotalquestion.getText().toString();
        totalTime = autoCompletetotaltime.getText().toString();
        answerIncluded = autoCompleteanswerIncluded.getText().toString();
        solutionIncluded = autoCompletesolutionIncluded.getText().toString();

        // set answerincluded and solution included in one string as(10,01,00,11) where 1 means yes, and 0 means no
        // first digid for answerincluded and second digit for solution included

        if(answerIncluded.equals("Yes") && solutionIncluded.equals("Yes")){
            answerOrSolutionIncluded = "11";
        }else if(answerIncluded.equals("Yes") && solutionIncluded.equals("No")){
            answerOrSolutionIncluded = "10";
        }else if(answerIncluded.equals("No") && solutionIncluded.equals("Yes")){
            answerOrSolutionIncluded = "01";
        }else if(answerIncluded.equals("No") && solutionIncluded.equals("No")){
            answerOrSolutionIncluded = "00";
        }



        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            final StorageReference sReference = storageReference.child(autoExamName+"/"+autoPorS+"/"+autoFileName+".docx");


            sReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();


                            //update firestore

                            if( autoExamName!= null && autoFileName!=null && autoPorS != null) {
                                update_firestore(sReference);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //display an error toast
            Toast.makeText(MainActivity.this, "Error! No file", Toast.LENGTH_SHORT).show();

        }
    }


    public void update_firestore(StorageReference sReference){
        // First of all get Url of our current file


        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();

                update_Firestore_withLink(url);

                Log.i("hiii",url);
                //downloadFile(MainActivity.this,"Mobile",".pdf",DIRECTORY_DOWNLOADS,url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "Error! Can't Get URL", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());

            }
        });

        // enter info of file to firestore



//        db.collection(autoExamName)
//                .add(myFieldMap)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

    }

    private void update_Firestore_withLink(String url) {

        String fileName;
        Map<String, Object> myFieldMap = new HashMap<>();   // map to store field info of firestore

        if(autoPorS.equals("Solutions")){
            fileName = autoExamName + " solution";
        }else if(autoPorS.equals("Papers")){
            fileName = autoExamName;
            myFieldMap.put(KEY_Q_STARTWITH, qstartWith);
            myFieldMap.put(KEY_O_STARTWITH, ostartWith);
            myFieldMap.put(KEY_O2_STARTWITH, o2startWith);
            myFieldMap.put(KEY_O3_STARTWITH, o3startWith);
            myFieldMap.put(KEY_O4_STARTWITH, o4startWith);
            myFieldMap.put(KEY_A_STARTWITH, astartWith);
            myFieldMap.put(KEY_S_STARTWITH, sstartWith);
            myFieldMap.put(KEY_MARKS, marks);
            myFieldMap.put(KEY_NEGATIVE_MARKS, negativemarks);
            myFieldMap.put(KEY_TOTAL_QUESTION, totalQuestion);
            myFieldMap.put(KEY_TOTAL_TIME, totalTime);
            myFieldMap.put(KEY_AS_INCLUDED, answerOrSolutionIncluded);

        }else{
            fileName = autoExamName + " answer";
        }

        myFieldMap.put(KEY_NAME, autoFileName);
        myFieldMap.put(KEY_LINK, url);


        db.collection(fileName).document(autoFileName).set(myFieldMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Information Updated on Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }


}
