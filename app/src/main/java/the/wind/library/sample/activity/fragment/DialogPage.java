package the.wind.library.sample.activity.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import the.wind.library.dialog.DangerTemplate;
import the.wind.library.dialog.ErrorTemplate;
import the.wind.library.dialog.InfoTemplate;
import the.wind.library.dialog.LoadingTemplate;
import the.wind.library.dialog.NotificationTemplate;
import the.wind.library.dialog.ProgressTemplate;
import the.wind.library.dialog.SimpleTaskTemplate;
import the.wind.library.dialog.SuccessTemplate;
import the.wind.library.dialog.WarnTemplate;
import the.wind.library.dialog.WindDialog;
import the.wind.library.sample.R;
import the.wind.library.utils.CWUtils;
import the.wind.library.view.Button;

public class DialogPage extends Fragment {

    // Tatsumaki layout
    private WindDialog _simpleDialog;
    private WindDialog _simpleTaskDialog;
    private WindDialog _infoDialog;
    private WindDialog _warnDialog;
    private WindDialog _dangerDialog;
    private WindDialog _errorDialog;
    private WindDialog _successDialog;
    private WindDialog _notifyDialog;
    private WindDialog _loadingDialog;
    private WindDialog _progressDialog1;
    private WindDialog _progressDialog2;
    private WindDialog _waitingDialog;

    // Fubuki layout
    private WindDialog _fubukiDialog;
    private WindDialog _fullScreenDialog;

    private boolean openNotificationFromSuccess = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // tatsumaki dialog
        simpleDialog(view);
        simpleTaskDialog(view);
        infoDialog(view);
        warnDialog(view);
        dangerDialog(view);
        errorDialog(view);
        successDialog(view);
        notifyDialog(view);
        loadingDialog(view);
        progressDialog1(view);
        progressDialog2(view);
        showWaiting(view);

        // fubuki dialog
        fubukiDialog(view);
        fullScreenDialog(view);

        // custom animation dialog
        customAnimDialog(view);
    }

    private void simpleDialog(View view) {
        _simpleDialog = new WindDialog(view.getContext());
        _simpleDialog.setTitle(R.string.nav_header_title);
        _simpleDialog.setContentText(R.string.nav_header_desc);
        _simpleDialog.addButton(Button.Type.GRAY, getString(R.string.button_close), null);
        view.findViewById(R.id._simpleDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _simpleDialog.show();
            }
        });
    }

    private void simpleTaskDialog(final View view) {
        _simpleTaskDialog = new WindDialog(view.getContext())
                .apply(SimpleTaskTemplate.instance())
                .setContentText("Try your best and take your money.")
                .setButtonText(1, "Yes");
        _simpleTaskDialog.setTitle("Buy a chance?");
        _simpleTaskDialog.buttons().get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _simpleTaskDialog.bundle().set("button", ((Button) v).textView().getText());
                _simpleTaskDialog.dismiss();
            }
        });
        _simpleTaskDialog.buttons().get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _simpleTaskDialog.bundle().set("button", ((Button) v).textView().getText());
                _simpleTaskDialog.dismissImmediately();
                _errorDialog.show();
            }
        });
        _simpleTaskDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String buttonText = (String) _simpleTaskDialog.bundle().get("button");
                Toast.makeText(view.getContext(), "dismiss - " + buttonText, Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id._simpleTaskDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _simpleTaskDialog.show();
            }
        });
    }

    private void infoDialog(View view) {
        _infoDialog = new WindDialog(view.getContext())
                .apply(InfoTemplate.instance())
                .setContentText(R.string.nav_header_desc);
        _infoDialog.setTitle(R.string.nav_header_title);
        view.findViewById(R.id._infoDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _infoDialog.show();
            }
        });
    }

    private void warnDialog(View view) {
        _warnDialog = new WindDialog(view.getContext())
                .apply(WarnTemplate.instance())
                .setContentText("Be-careful!\nThe data maybe lost due to this action")
                .setButtonText(1, "Migrate");
        _warnDialog.addButton(Button.Type.DANGER, "", Button.InlineIcon.TRASH).setMinimumWidth(0);
        _warnDialog.setTitle("Migrate Database");
        view.findViewById(R.id._warnDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _warnDialog.show();
            }
        });
    }

    private void dangerDialog(View view) {
        _dangerDialog = new WindDialog(view.getContext())
                .apply(DangerTemplate.instance())
                .setContentText("Won't be able to recover this love")
                .setButtonText(1, "Yes, delete it!");
        _dangerDialog.setTitle("Are you sure");
        _dangerDialog.buttons().get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dangerDialog.dismissImmediately();
                _successDialog.show();
                openNotificationFromSuccess = true;
            }
        });
        view.findViewById(R.id._dangerDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dangerDialog.show();
            }
        });
    }

    private void errorDialog(View view) {
        _errorDialog = new WindDialog(view.getContext())
                .apply(ErrorTemplate.instance())
                .setContentText("Something went wrong!");
        _errorDialog.setTitle("Oops...");
        view.findViewById(R.id._errorDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _errorDialog.show();
            }
        });
    }

    private void successDialog(View view) {
        _successDialog = new WindDialog(view.getContext())
                .apply(SuccessTemplate.instance())
                .setContentText("We're all travelling through time together, everyday of out lives");
        _successDialog.setTitle("Completed!");
        _successDialog.button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _successDialog.dismissImmediately();
                if (openNotificationFromSuccess) {
                    _notifyDialog.showImmediately();
                    openNotificationFromSuccess = false;
                }
            }
        });
        view.findViewById(R.id._successDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _successDialog.show();
            }
        });
    }

    private void notifyDialog(View view) {
        _notifyDialog = new WindDialog(view.getContext())
                .apply(NotificationTemplate.instance())
                .setContentText("All we can do is do our best to relish the remarkable ride.");
        _notifyDialog.setTitle("Lovemory!");
        view.findViewById(R.id._notifyDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _notifyDialog.show();
            }
        });
    }

    private void loadingDialog(View view) {
        _loadingDialog = new WindDialog(view.getContext())
                .apply(LoadingTemplate.instance())
                .setContentText("This process may take a long time");
        _loadingDialog.setTitle("Please wait!");
        view.findViewById(R.id._loadingDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _loadingDialog.show(5000);
            }
        });
    }

    private void progressDialog1(View view) {
        _progressDialog1 = new WindDialog(view.getContext())
                .apply(ProgressTemplate.instance());
        view.findViewById(R.id._progressDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _progressDialog1.show(5000);
            }
        });
    }

    private void progressDialog2(View view) {
        _progressDialog2 = new WindDialog(view.getContext())
                .apply(ProgressTemplate.instance());
        _progressDialog2.getLayout().setBackground(null);
        view.findViewById(R.id._progressDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _progressDialog2.show(5000);
            }
        });
    }

    private void showWaiting(View view) {
        _waitingDialog = new WindDialog(view.getContext())
                .apply(SimpleTaskTemplate.instance())
                .setContentText("This process may take a long time")
                .setButtonText(1, "Update");
        _waitingDialog.setTitle("Update version");
        _waitingDialog.buttons().get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _waitingDialog.waitMe();
                // do something here
                _waitingDialog.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        _waitingDialog.imDone(true);
                    }
                }, 5000);
            }
        });
        _waitingDialog.addButton(Button.Type.NEUTRAL, "Wait", null);
        _waitingDialog.buttons().get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _waitingDialog.waitMe(5000, false);
            }
        });
        view.findViewById(R.id._waitingDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _waitingDialog.show();
            }
        });
    }

    private WindDialog createFubukiDialog(View view) {
        final WindDialog dialog = new WindDialog(view.getContext(), WindDialog.LayoutType.FUBUKI);
        dialog.setTitle("The adventure");
        dialog
                .setIcon(R.drawable.treasure_map)
                .addButton(Button.Type.NEUTRAL, "Thinking", null)
                .setCustomIcon(R.drawable.ic_question)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.waitMe(3000, false);
                    }
                });
        dialog.addButton(Button.Type.SUCCESS, "Let's go", null).setCustomIcon(R.drawable.ic_rocket);
        dialog.setContentView(R.layout.custom_fubuki_dialog_content);
        return dialog;
    }

    private void fubukiDialog(View view) {
        _fubukiDialog = createFubukiDialog(view);
        view.findViewById(R.id._fubukiDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _fubukiDialog.setInOutAnimType(WindDialog.InOutAnimType.SWEET_ALERT);
                _fubukiDialog.show();
            }
        });
    }

    private void fullScreenDialog(View view) {
        _fullScreenDialog = createFubukiDialog(view);
        Size screenSize = CWUtils.getScreenSize(Objects.requireNonNull(getActivity()));
        _fullScreenDialog.setWidth(screenSize.getWidth()).setHeight(screenSize.getHeight());
        _fullScreenDialog.setLottieIcon(R.raw.cycle_ahead);
        _fullScreenDialog.setInOutAnimType(WindDialog.InOutAnimType.SLIDE_TOP_2_BOTTOM);
        view.findViewById(R.id._fullScreenDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _fullScreenDialog.show();
            }
        });
    }

    private void customAnimDialog(View view) {
        view.findViewById(R.id._animSweetAlert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _notifyDialog.setInOutAnimType(WindDialog.InOutAnimType.SWEET_ALERT);
                _notifyDialog.show();
            }
        });
        view.findViewById(R.id._animFade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _notifyDialog.setInOutAnimType(WindDialog.InOutAnimType.FADE);
                _notifyDialog.show();
            }
        });
        view.findViewById(R.id._animLeftRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _notifyDialog.setInOutAnimType(WindDialog.InOutAnimType.SLIDE_LEFT_2_RIGHT);
                _notifyDialog.show();
            }
        });
        view.findViewById(R.id._animTopBottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _fubukiDialog.setInOutAnimType(WindDialog.InOutAnimType.SLIDE_TOP_2_BOTTOM);
                _fubukiDialog.show();
            }
        });
    }

}