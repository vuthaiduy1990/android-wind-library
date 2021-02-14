package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.dialog.WindDialog;
import the.wind.library.menu.WindActionMenu;
import the.wind.library.sample.R;

public class ActionMenuPage extends Fragment {

    private WindActionMenu _shortActionMenu;
    private WindActionMenu _longActionMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createShortActionMenu(view);
        createLongActionMenu(view);
    }

    private void createShortActionMenu(View view) {
        _shortActionMenu = new WindActionMenu(view.getContext());
        initBaseMenuItem(_shortActionMenu);
        _shortActionMenu.setOnItemSelectListener(new WindActionMenu.OnItemSelectListener() {
            @Override
            public void onSelect(int id) {
                switch (id) {
                    case R.id.wind_menu_item_set_background:
                        Toast.makeText(getContext(), R.string.wind_menu_item_set_background, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.wind_menu_item_set_color:
                        Toast.makeText(getContext(), R.string.wind_menu_item_set_color, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.wind_menu_item_set_money:
                        Toast.makeText(getContext(), R.string.wind_menu_item_set_money, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.wind_menu_item_track_time:
                        Toast.makeText(getContext(), R.string.wind_menu_item_track_time, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.wind_menu_item_delete:
                        Toast.makeText(getContext(), R.string.wind_menu_item_delete, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        _shortActionMenu.setOnMenuConfigListener(new WindActionMenu.OnMenuConfigListener() {
            @Override
            public void onConfig(ViewGroup menuHolder) {
                View settingItem = menuHolder.findViewById(R.id.wind_menu_item_setting);
                settingItem.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id._shortMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _shortActionMenu.show();
            }
        });
    }

    private void createLongActionMenu(View view) {
        _longActionMenu = new WindActionMenu(view.getContext());
        _longActionMenu.setItemBackground(R.drawable.wl_button_background_neutral_light);
        _longActionMenu.setItemIconSize(R.dimen.wl_icon_big);
        _longActionMenu.setItemTextColor(R.color.wl_info_pressed);
        _longActionMenu.setItemTextSize(R.dimen.wl_text_big);
        _longActionMenu.setHeight(800);
        _longActionMenu.setGravity(Gravity.BOTTOM);
        _longActionMenu.setInOutAnimType(WindDialog.InOutAnimType.SLIDE_BOTTOM_2_TOP);
        _longActionMenu.setMarginBottom(50);
        initBaseMenuItem(_longActionMenu);
        for (int i = 0; i < 10; i++) {
            _longActionMenu.addItem(R.id.wind_menu_item_more, R.drawable.wl_ic_search, R.string.wind_menu_more_item);
        }
        view.findViewById(R.id._longMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _longActionMenu.show();
            }
        });
    }

    private void initBaseMenuItem(WindActionMenu menu) {
        menu.addItem(R.id.wind_menu_item_set_background, R.drawable.wl_ic_edit, R.string.wind_menu_item_set_background);
        menu.addItem(R.id.wind_menu_item_set_color, R.drawable.wl_ic_save, R.string.wind_menu_item_set_color);
        menu.addItem(R.id.wind_menu_item_set_money, R.drawable.wl_ic_lock, R.string.wind_menu_item_set_money);
        menu.addItem(R.id.wind_menu_item_track_time, R.drawable.wl_ic_ok, R.string.wind_menu_item_track_time);
        menu.addItem(R.id.wind_menu_item_setting, R.drawable.wl_ic_setting, R.string.wind_menu_item_setting);
        menu.addItem(R.id.wind_menu_item_delete, R.drawable.wl_ic_trash, R.string.wind_menu_item_delete);
    }
}