package ua.com.vando.apk_airplan;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Serial {
    JSONObject mJO;

    public Serial() {
        Clear();
    }

    public void Clear() {
        mJO = new JSONObject();
        Add("Data", new JSONArray());
    }

    public String GetData(){
        return mJO.toString();
    }

    public void Add(String aName, JSONObject aJO) {
        try {
            mJO.put(aName, aJO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Add(String aName, JSONArray aJA) {
        try {
            mJO.put(aName, aJA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AddData(JSONObject aJO) throws JSONException {
        mJO.getJSONArray("Data").put(aJO);
    }

    public void AddFunc(String aName, JSONArray aArgs) {
        JSONObject JO = new JSONObject();
        try {
            JO.put("Func", aName);
            JO.put("Args", aArgs);
            AddData(JO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AddFunc(String aName, int [] aArgs) {
        JSONArray JA = new JSONArray();
        for (int i = 0; i < aArgs.length; i++)
            JA.put(aArgs[i]);
        AddFunc(aName, JA);
    }

    public void AddFuncArr(String aName, int [] aPins, int [] aArgs) {
        JSONArray JA1 = new JSONArray();
        for (int i = 0; i < aPins.length; i++)
            JA1.put(aPins[i]);

        JSONArray JA2 = new JSONArray();
        JA2.put(JA1);
        for (int i = 0; i < aArgs.length; i++)
            JA2.put(aArgs[i]);
        AddFunc(aName, JA2);
    }

    public void SetLogLevel(int aValue) {
        AddFunc("SetLogLevel", new int [] {aValue});
    }

    public void Log(int aLevel, String aMsg) {
        JSONArray JA = new JSONArray();
        JA.put(aLevel);
        JA.put(aMsg);
        AddFunc("Log", JA);
    }

    public void GetPwmDuty() {
        AddFunc("GetPwmDuty", new int [] {});
    }

    public void SetPin(int aPin, int aValue) {
        AddFunc("SetPin",  new int [] { aPin, aValue});
    }

    public void SetPwmOff(int aPin) {
        AddFunc("SetPwmOff", new int [] {aPin});
    }

    public void SetPwmFreq(int aPin, int aValue) {
        AddFunc("SetPwmFreq", new int [] {aPin, aValue});
    }

    public void SetPwmDuty(int aPin, int aValue) {
        AddFunc("SetPwmDuty", new int [] {aPin, aValue});
    }

/*
    public void SetPinArr(int [] aPins, int aValue) {
        AddFuncArr("SetPinArr", aPins, new int[] {aValue});
    }

    public void SetPwmOffArr(int [] aPins) {
        AddFuncArr("SetPwmOffArr", aPins, new int[]{});
    }
*/
}
