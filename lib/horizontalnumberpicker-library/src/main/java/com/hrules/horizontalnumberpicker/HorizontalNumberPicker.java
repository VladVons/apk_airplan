package com.hrules.horizontalnumberpicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.view.KeyEvent.KEYCODE_0;
import static android.view.KeyEvent.KEYCODE_9;

public class HorizontalNumberPicker extends LinearLayout {
  private final static int MIN_UPDATE_INTERVAL = 50;

  private int curValue = 0, prevValue, maxValue, minValue;
  private int stepSize;
  private boolean showLeadingZeros;

  private Button buttonMinus;
  private Button buttonPlus;
  private TextView textValue;
  private SeekBar sbRange;

  private boolean autoInc;
  private boolean autoDec;

  private int updateInterval;

  private Handler updateIntervalHandler;

  private HorizontalNumberPickerListener listener;

  public HorizontalNumberPicker(Context context) {
    this(context, null);
  }

  public HorizontalNumberPicker(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (isInEditMode()) {
      return;
    }

    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    layoutInflater.inflate(R.layout.horizontal_number_picker, this);

    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalNumberPicker);
    Resources res = getResources();

    String buttonPlusText = typedArray.getString(R.styleable.HorizontalNumberPicker_plusButtonText);
    initButtonPlus(buttonPlusText != null ? buttonPlusText : res.getString(R.string.defaultButtonPlus));

    String buttonMinusText = typedArray.getString(R.styleable.HorizontalNumberPicker_minusButtonText);
    initButtonMinus(buttonMinusText != null ? buttonMinusText : res.getString(R.string.defaultButtonMinus));

    minValue = typedArray.getInt(R.styleable.HorizontalNumberPicker_minValue,  res.getInteger(R.integer.default_minValue));
    maxValue = typedArray.getInt(R.styleable.HorizontalNumberPicker_maxValue,  res.getInteger(R.integer.default_maxValue));

    updateInterval = typedArray.getInt(R.styleable.HorizontalNumberPicker_repeatDelay, res.getInteger(R.integer.default_updateInterval));
    stepSize = typedArray.getInt(R.styleable.HorizontalNumberPicker_stepSize, res.getInteger(R.integer.default_stepSize));
    showLeadingZeros = typedArray.getBoolean(R.styleable.HorizontalNumberPicker_showLeadingZeros, res.getBoolean(R.bool.default_showLeadingZeros));

    curValue = typedArray.getInt(R.styleable.HorizontalNumberPicker_value, res.getInteger(R.integer.default_value));
    typedArray.recycle();

    autoInc = false;
    autoDec = false;

    updateIntervalHandler = new Handler();

    textValue = (TextView) findViewById(R.id.text_value);
    textValue.setOnKeyListener(OnKeyListener);

    sbRange  = (SeekBar)  findViewById(R.id.sbRange);
    sbRange.setOnSeekBarChangeListener(OnSeekBarChangeListener);

    setValue();
  }

  private EditText.OnKeyListener OnKeyListener = new EditText.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode >= KEYCODE_0 && keyCode <= KEYCODE_9) {
                String StrValue = ((EditText) v).getText().toString();
                int Value = 0;
                try {
                    Value = Integer.parseInt(StrValue);
                } catch (Exception e) {}

                if (! SetValueCheck(Value)) {
                    setValue(Value);
                }
            }
            return false;
        }
  };

  private SeekBar.OnSeekBarChangeListener OnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setValue(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
  };

  private void initButtonPlus(String text) {
    buttonPlus = (Button) findViewById(R.id.button_plus);
    buttonPlus.setText(text);

    buttonPlus.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Inc();
      }
    });

    buttonPlus.setOnLongClickListener(new View.OnLongClickListener() {
      public boolean onLongClick(View v) {
        autoInc = true;
        updateIntervalHandler.post(new repeat());
        return false;
      }
    });

    buttonPlus.setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && autoInc) {
          autoInc = false;
        }
        return false;
      }
    });
  }

  private void initButtonMinus(String text) {
    buttonMinus = (Button) findViewById(R.id.button_minus);
    buttonMinus.setText(text);

    buttonMinus.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Dec();
      }
    });

    buttonMinus.setOnLongClickListener(new View.OnLongClickListener() {
      public boolean onLongClick(View v) {
        autoDec = true;
        updateIntervalHandler.post(new repeat());
        return false;
      }
    });

    buttonMinus.setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && autoDec) {
          autoDec = false;
        }
        return false;
      }
    });
  }

  public Button getButtonMinusView() {
    return buttonMinus;
  }

  public Button getButtonPlusView() {
    return buttonPlus;
  }

  public TextView getTextValueView() {
    return textValue;
  }

  public void Inc() {
    if (curValue < maxValue) {
      setValue(curValue + stepSize);
    }
  }

  public void Dec() {
    if (curValue > minValue) {
      setValue(curValue - stepSize);
    }
  }

  public int getValue() {
    return curValue;
  }

  public boolean SetValueCheck(int aValue) {
    if (aValue > maxValue) {
      curValue = maxValue;
    } else
    if (aValue < minValue) {
      curValue = minValue;
    }else {
      curValue = aValue;
    }

    return (curValue == aValue);
  }

  public void setValue(int aValue) {
    SetValueCheck(aValue);
    if (prevValue != aValue) {
      prevValue = aValue;
      setValue();
    }
  }

  private void setValue() {
      String formatter = "%0" + String.valueOf(maxValue).length() + "d";
      textValue.setText(showLeadingZeros ? String.format(formatter, curValue) : String.valueOf(curValue));
      if (listener != null) {
          listener.onHorizontalNumberPickerChanged(this, curValue);
      }
      sbRange.setProgress(curValue);
  }

  public int getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(int aValue) {
    maxValue = aValue;
    if (aValue < curValue) {
      curValue = aValue;
      setValue();
    }

    sbRange.setMax(aValue);
  }

  public int getMinValue() {
    return minValue;
  }

  public void setMinValue(int aValue) {
    minValue = aValue;
    if (aValue > curValue) {
      curValue = aValue;
      setValue();
    }
  }

  public int getStepSize() {
    return stepSize;
  }

  public void setStepSize(int stepSize) {
    stepSize = stepSize;
  }

  public void setShowLeadingZeros(boolean aValue) {
    showLeadingZeros = aValue;

    String formatter = "%0" + String.valueOf(maxValue).length() + "d";
    textValue.setText(showLeadingZeros ? String.format(formatter, curValue) : String.valueOf(curValue));
  }

  public long getOnLongPressUpdateInterval() {
    return updateInterval;
  }

  public void setOnLongPressUpdateInterval(int intervalMillis) {
    if (intervalMillis < MIN_UPDATE_INTERVAL) {
      intervalMillis = MIN_UPDATE_INTERVAL;
    }
    updateInterval = intervalMillis;
  }

  public void setListener(HorizontalNumberPickerListener aListener) {
    listener = aListener;
  }

  private class repeat implements Runnable {
    public void run() {
      if (autoInc) {
        Inc();
        updateIntervalHandler.postDelayed(new repeat(), updateInterval);
      } else if (autoDec) {
        Dec();
        updateIntervalHandler.postDelayed(new repeat(), updateInterval);
      }
    }
  }
}
