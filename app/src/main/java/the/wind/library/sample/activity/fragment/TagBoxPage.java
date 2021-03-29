package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.sample.R;
import the.wind.library.view.TagBox;

public class TagBoxPage extends Fragment {

    private EditText _tagInput;
    private TagBox _tagBox1;
    private TagBox _tagBox2;
    private String[] tags = new String[]{
            "android", "react-js", "spring-boot", "color the wind",
            "The memories", "washed over her like waves. Something sad to me",
            "as she felt", "them come back"
    };
    private String[] colors = new String[]{
            "#e3d4d3", "#5d9f69", "#c990a5", "#4976a0", "#b25542"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // default tag box
        defaultTagBox(view);

        // Custom tag box
        customTagBox(view);
    }

    private void defaultTagBox(View view) {
        _tagBox1 = view.findViewById(R.id._tagBox1);
        _tagBox1.setTags(new HashSet<>(Arrays.asList(tags)));
        _tagBox1.setOnItemClickListener(new TagBox.OnItemClickListener() {
            @Override
            public void onClick(View view, String tag) {
                Toast.makeText(getContext(), tag, Toast.LENGTH_SHORT).show();
            }
        });
        _tagBox1.setOnItemRemoveListener(new TagBox.OnItemRemoveListener() {
            @Override
            public boolean onRemove(View view, String tag) {
                if (tag.equals("android")) {
                    Toast.makeText(getContext(), "Cannot remove this item", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Toast.makeText(getContext(), String.format("%s is removed", tag), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Add item
        _tagInput = view.findViewById(R.id._tagInput);
        view.findViewById(R.id._icTagEnter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _tagBox1.add(_tagInput.getText().toString().trim());
                _tagInput.setText("");
            }
        });

    }

    private void customTagBox(View view) {
        _tagBox2 = view.findViewById(R.id._tagBox2);
        _tagBox2.setColors(colors);
        _tagBox2.setTags(new HashSet<>(Arrays.asList(tags)));
    }
}