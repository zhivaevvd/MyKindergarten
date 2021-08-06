package com.example.mykindergarten.ui.schedule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.mykindergarten.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import Modules.Message;
import Modules.Task;

public class ScheduleFragment extends Fragment {

    private TabLayout tabLayout;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference scheduleRefForAgeGroup_5 = database.getReference().child("Schedule_for_ageGroup_5");
    private Context context;
    private final String tag = "TAG";
    private FirebaseListAdapter<Task> adapter;
    private ListView listView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_schedule, container, false);

        tabLayout = root.findViewById(R.id.tabLayout);
        context = getContext();
        listView = root.findViewById(R.id.tasks_list);

        scheduleRefForAgeGroup_5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String date = ds.getKey();

                    date = date.replaceAll(" ", ".");

                    tabLayout.addTab(tabLayout.newTab().setText(date));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               String value = tab.getText().toString();
               value = value.replace('.', ' ');

                String finalValue = value;
                scheduleRefForAgeGroup_5.child(value).addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String theme = snapshot.child("theme").getValue(String.class);
                        TextView header = root.findViewById(R.id.theme);
                        header.setText(theme);

                        adapter = new FirebaseListAdapter<Task>((Activity) context, Task.class, R.layout.task_item, scheduleRefForAgeGroup_5.child(finalValue).child("Lessons")) {
                            @Override
                            protected void populateView(View v, Task model, int position) {
                                TextView name, task_text, link, tv;
                                LinearLayout linearLayout = v.findViewById(R.id.test);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);

                                name = v.findViewById(R.id.item_title);
                                task_text = v.findViewById(R.id.task_text);
                                link = v.findViewById(R.id.link);

                                name.setText(model.getName());
                                task_text.setText(model.getTask());

                                String tmp = model.getUrl();
                                String[] links = tmp.split(" ");

                                String text = String.format("<a href=\"%s\">Ссылка</a> ", links[0]);
                                link.setText(Html.fromHtml(text + " "));
                                link.setMovementMethod(LinkMovementMethod.getInstance());
                               /* tv = new TextView(context);

                                for (int i = 1; i < links.length; i++){
                                    if (tv.getParent() != null){
                                        ((ViewGroup)tv.getParent()).removeView(tv);
                                    }
                                        String s = String.format("<a href=\"%s\">Ссылка</a> ", links[i]);
                                        tv.setText(Html.fromHtml(text + " "));
                                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                                        linearLayout.addView(tv, params);
                                }*/

                            }
                        };

                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }


                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return root;
    }

}