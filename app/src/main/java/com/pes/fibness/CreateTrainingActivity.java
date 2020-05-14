package com.pes.fibness;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;


public class CreateTrainingActivity extends AppCompatActivity {

    private Boolean isNew;
    private String titleTraining = "";
    private ListView exerciseList;
    private ArrayList<Exercise> exercise = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concrete_training);
        getExtras();

        exercise = User.getInstance().getExerciseList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titleTraining);
        getSupportActionBar().setSubtitle(User.getInstance().getTrainingDesc(titleTraining));

        exerciseList = (ListView) findViewById(R.id.ExerciseList);

        refreshList();

        Button add_ex = (Button) findViewById(R.id.AddExer);
        add_ex.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Entra");
                showChooseOption();
                isNew = false;
                refreshList();
            }
        });

        exerciseList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int Desc = User.getInstance().getExerciseNamePos(position);
                if(Desc >= 0) showEditExBox(position);
                else showEditExBoxPers(position);
                return true;
            }
        });

    }

    private void showChooseOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTrainingActivity.this);
        builder.setView(R.layout.choose_type_training);
        final AlertDialog dialog = builder.create();
        dialog.show();
        Button btnSimple = (Button) dialog.findViewById(R.id.Simple);
        Button btnCustom = (Button) dialog.findViewById(R.id.Custom);
        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewExercise();
                dialog.dismiss();
            }
        });
        btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewExercisePers();
                dialog.dismiss();
            }
        });
    }

    private void showEditExBox(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTrainingActivity.this);
        builder.setView(R.layout.input_edit_exercise);
        builder.setTitle("Exercise");
        final AlertDialog dialog = builder.create();
        dialog.show();
        final Spinner txtNameS = (Spinner) dialog.findViewById(R.id.ExerciseTitle_edit);
        int posEx = User.getInstance().getExerciseNamePos(position);
        txtNameS.setSelection(posEx);
        final EditText numRest = (EditText) dialog.findViewById(R.id.num_Rest_edit);
        numRest.setText(exercise.get(position).NumRest);
        final EditText numSeries = (EditText) dialog.findViewById(R.id.num_Series_edit);
        numSeries.setText(exercise.get(position).NumSerie);
        final EditText numRepet = (EditText) dialog.findViewById(R.id.num_Repet_edit);
        numRepet.setText(exercise.get(position).NumRepet);
        Button btndone = (Button) dialog.findViewById(R.id.btn_done_edit);
        Button btndelete = (Button) dialog.findViewById(R.id.btn_delete_edit);
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correct = true;
                String txtName = (String) txtNameS.getSelectedItem();
                if (txtName.equals("Select exercise")) {
                    correct = false;
                }
                if (numRest.getText().toString().trim().length() == 0) {
                    numRest.setError("Please, add a number");
                    correct = false;
                }
                if (numRepet.getText().toString().trim().length() == 0) {
                    numRepet.setError("Please, add a number");
                    correct = false;
                }
                if (numSeries.getText().toString().trim().length() == 0) {
                    numSeries.setError("Please, add a number");
                    correct = false;
                }
                if (correct) {
                    Exercise t2 = new Exercise();
                    t2.TitleEx = txtName;
                    t2.NumSerie = numSeries.getText().toString();
                    t2.NumRest = numRest.getText().toString();
                    t2.NumRepet = numRepet.getText().toString();
                    t2.Pos = User.getInstance().getExerciseNamePos(position);
                    int idExercise = User.getInstance().getExerciseID(position);
                    t2.id = idExercise;

                    User.getInstance().updateExercise(position, t2);

                    ConnetionAPI c = new ConnetionAPI(getApplicationContext(), "http://10.4.41.146:3001/exercise/" + idExercise );
                    c.updateTrainingExercises(t2.TitleEx, "", t2.Pos, Integer.parseInt(t2.NumRest), Integer.parseInt(t2.NumSerie), Integer.parseInt(t2.NumRepet));

                    refreshList();
                    dialog.dismiss();
                }
            }
        });
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int idExercise = User.getInstance().getExerciseID(position);
                ConnetionAPI c = new ConnetionAPI(getApplicationContext(), "http://10.4.41.146:3001/exercise/" + idExercise );
                c.deleteTrainingExercises();

                User.getInstance().deleteExercise(position);

                refreshList();
                dialog.dismiss();
            }
        });

    }

    private void showEditExBoxPers(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTrainingActivity.this);
        builder.setView(R.layout.input_edit_exercise_pers);
        builder.setTitle("Exercise");
        final AlertDialog dialog = builder.create();
        dialog.show();
        final EditText txtName = (EditText) dialog.findViewById(R.id.ExerciseTitle_edit);
        txtName.setText(exercise.get(position).TitleEx);
        final EditText numRest = (EditText) dialog.findViewById(R.id.num_Rest_edit);
        numRest.setText(exercise.get(position).NumRest);
        final EditText numSeries = (EditText) dialog.findViewById(R.id.num_Series_edit);
        numSeries.setText(exercise.get(position).NumSerie);
        final EditText numRepet = (EditText) dialog.findViewById(R.id.num_Repet_edit);
        numRepet.setText(exercise.get(position).NumRepet);
        final EditText txtDesc = (EditText) dialog.findViewById(R.id.editDescEx);
        txtDesc.setText(exercise.get(position).Desc);
        Button btndone = (Button) dialog.findViewById(R.id.btn_done_edit);
        Button btndelete = (Button) dialog.findViewById(R.id.btn_delete_edit);
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correct = true;
                if (txtName.getText().toString().trim().length() == 0) {
                    txtName.setError("Please, add a name");
                    correct = false;
                }
                if (numRest.getText().toString().trim().length() == 0) {
                    numRest.setError("Please, add a number");
                    correct = false;
                }
                if (numRepet.getText().toString().trim().length() == 0) {
                    numRepet.setError("Please, add a number");
                    correct = false;
                }
                if (numSeries.getText().toString().trim().length() == 0) {
                    numSeries.setError("Please, add a number");
                    correct = false;
                }
                if (correct) {
                    Exercise t2 = new Exercise();
                    t2.TitleEx = txtName.getText().toString();
                    t2.NumSerie = numSeries.getText().toString();
                    t2.NumRest = numRest.getText().toString();
                    t2.NumRepet = numRepet.getText().toString();
                    t2.Desc = txtDesc.getText().toString();
                    t2.Pos = User.getInstance().getExerciseNamePos(position);
                    int idExercise = User.getInstance().getExerciseID(position);
                    t2.id = idExercise;

                    User.getInstance().updateExercise(position, t2);

                    ConnetionAPI c = new ConnetionAPI(getApplicationContext(), "http://10.4.41.146:3001/exercise/" + idExercise );
                    c.updateTrainingExercises(t2.TitleEx, t2.Desc, t2.Pos, Integer.parseInt(t2.NumRest), Integer.parseInt(t2.NumSerie), Integer.parseInt(t2.NumRepet));

                    refreshList();
                    dialog.dismiss();
                }
            }
        });
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int idExercise = User.getInstance().getExerciseID(position);
                System.out.println("IDDDD " + idExercise);
                ConnetionAPI c = new ConnetionAPI(getApplicationContext(), "http://10.4.41.146:3001/exercise/" + idExercise );
                c.deleteTrainingExercises();

                User.getInstance().deleteExercise(position);

                refreshList();
                dialog.dismiss();
            }
        });

    }

    private void refreshList() {
        exerciseList.setAdapter(new Exercise_Adap(this, exercise, isNew));
    }

    private void showNewExercisePers() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTrainingActivity.this);
        builder.setView(R.layout.input_new_exercise_pers);
        builder.setTitle("Exercise");
        final AlertDialog dialog = builder.create();
        dialog.show();
        final EditText txtName = (EditText) dialog.findViewById(R.id.ExerciseTitle);
        final EditText numRest = (EditText) dialog.findViewById(R.id.num_Rest);
        final EditText numSeries = (EditText) dialog.findViewById(R.id.num_Series);
        final EditText numRepet = (EditText) dialog.findViewById(R.id.num_Rept);
        final EditText txtDesc = (EditText) dialog.findViewById(R.id.newDescEx);
        Button bt = (Button) dialog.findViewById(R.id.btn_done);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correct = true;
                if (txtName.getText().toString().trim().length() == 0) {
                    txtName.setError("Please, add a name");
                    correct = false;
                }
                if (numRepet.getText().toString().trim().length() == 0) {
                    numRepet.setError("Please, add a number");
                    correct = false;
                }
                if (numRest.getText().toString().trim().length() == 0) {
                    numRest.setError("Please, add a number");
                    correct = false;
                }
                if (numSeries.getText().toString().trim().length() == 0) {
                    numSeries.setError("Please, add a number");
                    correct = false;
                }
                if (correct) {
                    Exercise t2 = new Exercise();
                    t2.TitleEx = txtName.getText().toString();
                    t2.NumSerie = numSeries.getText().toString();
                    t2.NumRest = numRest.getText().toString();
                    t2.NumRepet = numRepet.getText().toString();
                    t2.Desc = txtDesc.getText().toString();
                    t2.Pos = -1;
                    t2.id = -1;

                    User.getInstance().addExercise(t2);

                    int pos = User.getInstance().sizeExerciseList();

                    int idTraining = User.getInstance().getTrainingID(titleTraining);
                    ConnetionAPI c = new ConnetionAPI(getApplicationContext(), "http://10.4.41.146:3001/exercise");
                    c.postTrainingExercises(idTraining, t2.TitleEx, t2.Desc, t2.Pos, Integer.parseInt(t2.NumRest), Integer.parseInt(t2.NumSerie), Integer.parseInt(t2.NumRepet), pos-1);

                    refreshList();
                    dialog.dismiss();
                }
            }
        });
    }

    private void showNewExercise() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTrainingActivity.this);
        builder.setView(R.layout.input_new_exercise);
        builder.setTitle("Exercise");
        final AlertDialog dialog = builder.create();
        dialog.show();
        final Spinner txtNameS = (Spinner) dialog.findViewById(R.id.ExerciseTitle);
        final EditText numRest = (EditText) dialog.findViewById(R.id.num_Rest);
        final EditText numSeries = (EditText) dialog.findViewById(R.id.num_Series);
        final EditText numRepet = (EditText) dialog.findViewById(R.id.num_Rept);
        Button bt = (Button) dialog.findViewById(R.id.btn_done);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correct = true;
                String txtName = (String) txtNameS.getSelectedItem();
                if (txtName.equals("Select exercise")) {
                    correct = false;
                }
                if (numRepet.getText().toString().trim().length() == 0) {
                    numRepet.setError("Please, add a number");
                    correct = false;
                }
                if (numRest.getText().toString().trim().length() == 0) {
                    numRest.setError("Please, add a number");
                    correct = false;
                }
                if (numSeries.getText().toString().trim().length() == 0) {
                    numSeries.setError("Please, add a number");
                    correct = false;
                }
                if (correct) {
                    Exercise t2 = new Exercise();
                    t2.TitleEx = txtName;
                    t2.NumSerie = numSeries.getText().toString();
                    t2.NumRest = numRest.getText().toString();
                    t2.NumRepet = numRepet.getText().toString();
                    t2.Pos = txtNameS.getSelectedItemPosition();
                    t2.id = -1;

                    User.getInstance().addExercise(t2);

                    int pos = User.getInstance().sizeExerciseList();

                    int idTraining = User.getInstance().getTrainingID(titleTraining);
                    ConnetionAPI c = new ConnetionAPI(getApplicationContext(), "http://10.4.41.146:3001/exercise");
                    c.postTrainingExercises(idTraining, t2.TitleEx, "", t2.Pos, Integer.parseInt(t2.NumRest), Integer.parseInt(t2.NumSerie), Integer.parseInt(t2.NumRepet), pos-1);

                    refreshList();
                    dialog.dismiss();
                }
            }
        });
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        isNew = extras.getBoolean("new");
        titleTraining = extras.getString("title");
    }

}
