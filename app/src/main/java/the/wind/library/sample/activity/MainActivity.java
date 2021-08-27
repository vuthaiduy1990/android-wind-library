package the.wind.library.sample.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import the.wind.library.anim.PageTransformerType;
import the.wind.library.sample.R;
import the.wind.library.sample.activity.fragment.CalendarPage;
import the.wind.library.sample.activity.fragment.RecycleViewPage;
import the.wind.library.view.WindRecycleView;

public class MainActivity extends AppCompatActivity {

    private final int[] NAVIGATION_ITEMS = new int[]{
            R.id._navHomeMenu,
            R.id._navColorMenu,
            R.id._navDimenMenu,
            R.id._navTextMenu,
            R.id._navCheckboxMenu,
            R.id._navButtonMenu,
            R.id._navDialogMenu,
            R.id._navCurrencyDialogMenu,
            R.id._navRecycleViewMenu,
            R.id._navActionMenu,
            R.id._navSearchBoxMenu,
            R.id._navTagBoxMenu,
            R.id._navCalendarMenu,
    };
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create toolbar
        Toolbar toolbar = findViewById(R.id._toolbar);
        setSupportActionBar(toolbar);

        // Create drawer
        DrawerLayout drawer = findViewById(R.id._drawerLayout);
        NavigationView navigationView = findViewById(R.id._navigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(NAVIGATION_ITEMS)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id._navHostFragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id._navHostFragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_action_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        NavController navController = Navigation.findNavController(this, R.id._navHostFragment);
        NavDestination navDes = navController.getCurrentDestination();

        // Show action item respective to recycle view page
        boolean rvItemVisible = navDes != null && navDes.getId() == R.id._navRecycleViewMenu;
        menu.findItem(R.id.action_menu_recycle_list_view).setVisible(rvItemVisible);
        menu.findItem(R.id.action_menu_recycle_grid_view).setVisible(rvItemVisible);
        menu.findItem(R.id.action_menu_recycle_anim_sweet_alert).setVisible(rvItemVisible);
        menu.findItem(R.id.action_menu_recycle_anim_fade).setVisible(rvItemVisible);
        menu.findItem(R.id.action_menu_recycle_anim_left_right).setVisible(rvItemVisible);
        menu.findItem(R.id.action_menu_recycle_anim_right_left).setVisible(rvItemVisible);
        menu.findItem(R.id.action_menu_recycle_anim_right_left).setVisible(rvItemVisible);
        menu.findItem(R.id.action_menu_recycle_enable_swap_position).setVisible(rvItemVisible);

        // Show action item for Calendar view page
        boolean calendarItemVisible = navDes != null && navDes.getId() == R.id._navCalendarMenu;
        menu.findItem(R.id.action_menu_calendar_btf_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_ftb_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_cube_in_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_cube_out_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_depth_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_flip_horizontal_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_flip_vertical_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_rotate_down_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_rotate_up_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_tablet_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_zoom_in_transform).setVisible(calendarItemVisible);
        menu.findItem(R.id.action_menu_calendar_zoom_out_transform).setVisible(calendarItemVisible);


        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment hostFrag = getSupportFragmentManager().findFragmentById(R.id._navHostFragment);
        Fragment currentPage = null;
        if (hostFrag != null) {
            currentPage = hostFrag.getChildFragmentManager().getFragments().get(0);
        }

        switch (item.getItemId()) {
            case R.id.action_menu_settings:
                Toast.makeText(this, R.string.action_menu_settings, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_menu_recycle_list_view:
                if (currentPage instanceof RecycleViewPage) {
                    ((RecycleViewPage) currentPage).changeViewType(RecycleViewPage.ViewType.LIST_VIEW);
                }
                break;
            case R.id.action_menu_recycle_grid_view:
                if (currentPage instanceof RecycleViewPage) {
                    ((RecycleViewPage) currentPage).changeViewType(RecycleViewPage.ViewType.GRID_VIEW);
                }
                break;
            case R.id.action_menu_recycle_anim_sweet_alert:
                if (currentPage instanceof RecycleViewPage) {
                    ((RecycleViewPage) currentPage).changeLayoutAnim(WindRecycleView.LayoutAnim.SWEET_ALERT);
                }
                break;
            case R.id.action_menu_recycle_anim_fade:
                if (currentPage instanceof RecycleViewPage) {
                    ((RecycleViewPage) currentPage).changeLayoutAnim(WindRecycleView.LayoutAnim.FADE);
                }
                break;
            case R.id.action_menu_recycle_anim_left_right:
                if (currentPage instanceof RecycleViewPage) {
                    ((RecycleViewPage) currentPage).changeLayoutAnim(WindRecycleView.LayoutAnim.LEFT_2_RIGHT);
                }
                break;
            case R.id.action_menu_recycle_anim_right_left:
                if (currentPage instanceof RecycleViewPage) {
                    ((RecycleViewPage) currentPage).changeLayoutAnim(WindRecycleView.LayoutAnim.RIGHT_2_LEFT);
                }
                break;
            case R.id.action_menu_recycle_enable_swap_position:
                if (currentPage instanceof RecycleViewPage) {
                    ((RecycleViewPage) currentPage).enableSwapPosition();
                }
                break;
            case R.id.action_menu_calendar_btf_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.BACKGROUND_TO_FOREGROUND);
                }
                break;
            case R.id.action_menu_calendar_ftb_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.FOREGROUND_TO_BACKGROUND);
                }
                break;
            case R.id.action_menu_calendar_cube_in_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.CUBE_IN);
                }
                break;
            case R.id.action_menu_calendar_cube_out_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.CUBE_OUT);
                }
                break;
            case R.id.action_menu_calendar_depth_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.DEPTH);
                }
                break;
            case R.id.action_menu_calendar_flip_horizontal_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.FLIP_HORIZONTAL);
                }
                break;
            case R.id.action_menu_calendar_flip_vertical_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.FLIP_VERTICAL);
                }
                break;
            case R.id.action_menu_calendar_rotate_down_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.ROTATE_DOWN);
                }
                break;
            case R.id.action_menu_calendar_rotate_up_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.ROTATE_UP);
                }
                break;
            case R.id.action_menu_calendar_tablet_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.TABLET);
                }
                break;
            case R.id.action_menu_calendar_zoom_in_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.ZOOM_IN);
                }
                break;
            case R.id.action_menu_calendar_zoom_out_transform:
                if (currentPage instanceof CalendarPage) {
                    ((CalendarPage) currentPage).setPageTransformer(PageTransformerType.ZOOM_OUT);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
