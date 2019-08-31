package the.wind.library.sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import the.wind.library.CWFloatingActionMenu;
import the.wind.library.sample.R;

public class MainActivity extends AppCompatActivity {

    private CWFloatingActionMenu _actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init floating action menu
        _actionMenu = findViewById(R.id.fbActionMenu);
        _actionMenu.newMenuItem("setting", R.string.app_name, R.mipmap.ic_launcher);
        _actionMenu.newMenuItem("checklist", R.string.app_name, R.mipmap.ic_launcher);
    }


}
