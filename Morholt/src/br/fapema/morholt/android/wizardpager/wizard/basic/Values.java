/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.fapema.morholt.android.wizardpager.wizard.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import br.fapema.morholt.collect.collectendpoint.model.JsonMap;

/**
 * This class is used to store a set of values that the {@link ContentResolver}
 * can process.
 * 
 * It was created just because android contentvalues are not serializable
 */
public class Values implements Parcelable {
	private static final long serialVersionUID = 8661965189807090769L;

	public static final String LAST_UPDATE_ON_SYSTEM_COLUMN = "lastUpdateOnSystem";
	public static final String TAG = "ContentValues";

    /** Holds the actual values */
    protected HashMap<String, Object> mValues;

    public static final String ID_COLUMN = "_id";
	public static final String PARENT_ID = "PARENT_ID";

	protected static final String ID = "_id";    
    /**
     * Creates an empty set of values using the default initial size
     */
    public Values() {
        // Choosing a default size of 8 based on analysis of typical
        // consumption by applications.
        mValues = new HashMap<String, Object>(8);
        mValues.put(br.fapema.morholt.android.basic.Creator.SENT_TO_SERVER, "0");
        mValues.put(br.fapema.morholt.android.basic.Creator.PHOTO_SENT_TO_SERVER, "0");
    }
    
    public Long getId() {
    	return getAsLong(ID_COLUMN);
    }
    
    public void setId(Integer id) {
    	put(ID_COLUMN, id);
    }
    
    public Values(JsonMap map) {
    	mValues = new HashMap<String, Object>(8);
    	for(Map.Entry<String, Object> entry : map.entrySet()) {
    		mValues.put(entry.getKey(), entry.getValue());
    	}
    }
    

    public HashMap<String, Object> getMValues() {
    	return mValues;
    }
    
    public JsonMap toJsonMap() {
    	JsonMap jsonMap = new JsonMap();
    	jsonMap.putAll(mValues);
		return jsonMap;
    }

    /**
     * Creates an empty set of values using the given initial size
     *
     * @param size the initial size of the set of values
     */
    public Values(int size) {
        mValues = new HashMap<String, Object>(size, 1.0f);
    }

    /**
     * Creates a set of values copied from the given set
     *
     * @param from the values to copy
     */
    public Values(Values from) {
        mValues = new HashMap<String, Object>(from.mValues);
    }
    
    public Values (ContentValues from) {
    	//ContentValues contentValues = new ContentValues();
    	 mValues = new HashMap<String, Object>();
    	for(String key : from.keySet()) {
    		Object object = from.get(key);
    		if(object instanceof String) {
    			mValues.put(key, (String)object); 
    		}
    		else if (object instanceof Boolean) {
    			mValues.put(key, (Boolean)object);
    		}
    		else if (object instanceof Long) {
    			mValues.put(key, (Long)object);
    		}
    		else if (object == null) {
    			mValues.put(key, null); 
    		}
    		else {
    			throw new RuntimeException("not developed: " + object.getClass()); // TODO
    		}
    	}
    }
    
    public ContentValues toContentValues() {
    	ContentValues contentValues = new ContentValues();
    	for(String key : mValues.keySet()) {
    		Object object = mValues.get(key);
    		if(object instanceof String) {
    			contentValues.put(key, (String)object); 
    		}
    		else if (object instanceof Boolean) {
    			contentValues.put(key, (Boolean)object);
    		}
    		else if (object instanceof Integer) {
    			contentValues.put(key, (Integer)object);
    		}
    		else if (object instanceof Long) {
    			contentValues.put(key, (Long)object);
    		}
    		else if (object instanceof ArrayList) {
    			// do nothing
    		}
    		else if (object == null) {
    			contentValues.putNull(key); 
    		}
    		else {
    			throw new RuntimeException("not developed: " + object.getClass()); //TODO
    		}
    	}
   // 	if(getParentValues() != null) {
   // 		contentValues.put(PARENT_ID, getParentValues().getId());
   // 	}
    	return contentValues;
    }
    
    /**
     * Creates a set of values copied from the given HashMap. This is used
     * by the Parcel unmarshalling code.
     *
     * @param values the values to start with
     * {@hide}
     */
    private Values(HashMap<String, Object> values) {
        mValues = values;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Values)) {
            return false;
        }
        return mValues.equals(((Values) object).mValues);
    }
    
    
    @Override
    public int hashCode() {
        return mValues.hashCode();
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, String value) {
        mValues.put(key, value);
    }

    /**
     * Adds all values from the passed in ContentValues.
     *
     * @param other the ContentValues from which to copy
     */
    public void putAll(Values other) {
        mValues.putAll(other.mValues);
    }

    
    public void put(String key, List<Values> myContentValuesWizards) {
    	mValues.put(key, myContentValuesWizards);
    }
    
    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Byte value) {
        mValues.put(key, value);
    }

    public void putObject(String key, Object value) {
        mValues.put(key, value);
    }
    
    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Short value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Integer value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Long value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Float value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Double value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Boolean value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, byte[] value) {
        mValues.put(key, value);
    }

    /**
     * Adds a null value to the set.
     *
     * @param key the name of the value to make null
     */
    public void putNull(String key) {
        mValues.put(key, null);
    }

    /**
     * Returns the number of values.
     *
     * @return the number of values
     */
    public int size() {
        return mValues.size();
    }

    /**
     * Remove a single value.
     *
     * @param key the name of the value to remove
     */
    public void remove(String key) {
        mValues.remove(key);
    }

    /**
     * Removes all values.
     */
    public void clear() {
        mValues.clear();
    }

    /**
     * Returns true if this object has the named value.
     *
     * @param key the value to check for
     * @return {@code true} if the value is present, {@code false} otherwise
     */
    public boolean containsKey(String key) {
        return mValues.containsKey(key);
    }

    /**
     * Gets a value. Valid value types are {@link String}, {@link Boolean}, and
     * {@link Number} implementations.
     *
     * @param key the value to get
     * @return the data for the value
     */
    public Object get(String key) {
        return mValues.get(key);
    }
    
    public List<Values> getAsValuesList(String key) {
    	return (List<Values>) mValues.get(key);
    }

    /**
     * Gets a value and converts it to a String.
     *
     * @param key the value to get
     * @return the String for the value
     */
    public String getAsString(String key) {
        Object value = mValues.get(key);
        return value != null ? value.toString() : null;
    }
    
    public String getAsText(String key) {
        Object value = mValues.get(key);
        return value != null ? value.toString() : "(vazio)";
    	
    }

    /**
     * Gets a value and converts it to a Long.
     *
     * @param key the value to get
     * @return the Long value, or null if the value is missing or cannot be converted
     */
    public Long getAsLong(String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).longValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Long.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Long value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + key + " to a Long: " + value, e);
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to an Integer.
     *
     * @param key the value to get
     * @return the Integer value, or null if the value is missing or cannot be converted
     */
    public Integer getAsInteger(String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).intValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Integer.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Integer value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + key + " to a Integer: " + value, e);
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Short.
     *
     * @param key the value to get
     * @return the Short value, or null if the value is missing or cannot be converted
     */
    public Short getAsShort(String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).shortValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Short.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Short value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + key + " to a Short: " + value, e);
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Byte.
     *
     * @param key the value to get
     * @return the Byte value, or null if the value is missing or cannot be converted
     */
    public Byte getAsByte(String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).byteValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Byte.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Byte value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + key + " to a Byte: " + value, e);
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Double.
     *
     * @param key the value to get
     * @return the Double value, or null if the value is missing or cannot be converted
     */
    public Double getAsDouble(String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).doubleValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Double.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Double value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + key + " to a Double: " + value, e);
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Float.
     *
     * @param key the value to get
     * @return the Float value, or null if the value is missing or cannot be converted
     */
    public Float getAsFloat(String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).floatValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Float.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Float value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + key + " to a Float: " + value, e);
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Boolean.
     *
     * @param key the value to get
     * @return the Boolean value, or null if the value is missing or cannot be converted
     */
    public Boolean getAsBoolean(String key) {
        Object value = mValues.get(key);
        try {
            return (Boolean) value;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                return Boolean.valueOf(value.toString());
            } else if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            } else {
                Log.e(TAG, "Cannot cast value for " + key + " to a Boolean: " + value, e);
                return null;
            }
        }
    }

    /**
     * Gets a value that is a byte array. Note that this method will not convert
     * any other types to byte arrays.
     *
     * @param key the value to get
     * @return the byte[] value, or null is the value is missing or not a byte[]
     */
    public byte[] getAsByteArray(String key) {
        Object value = mValues.get(key);
        if (value instanceof byte[]) {
            return (byte[]) value;
        } else {
            return null;
        }
    }

    /**
     * Returns a set of all of the keys and values
     *
     * @return a set of all of the keys and values
     */
    public Set<Map.Entry<String, Object>> valueSet() {
        return mValues.entrySet();
    }

    /**
     * Returns a set of all of the keys
     *
     * @return a set of all of the keys
     */
    public Set<String> keySet() {
        return mValues.keySet();
    }

    public static final Parcelable.Creator<Values> CREATOR =
            new Parcelable.Creator<Values>() {
        @SuppressWarnings({"deprecation", "unchecked"})
        public Values createFromParcel(Parcel in) {
            // TODO - what ClassLoader should be passed to readHashMap?
            HashMap<String, Object> values = in.readHashMap(null);
            return new Values(values);
        }

        public Values[] newArray(int size) {
            return new Values[size];
        }
    };


    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("deprecation")
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeMap(mValues);
    }

    /**
     * Unsupported, here until we get proper bulk insert APIs.
     * {@hide}
     */
    @Deprecated
    public void putStringArrayList(String key, ArrayList<String> value) {
        mValues.put(key, value);
    }

    /**
     * Unsupported, here until we get proper bulk insert APIs.
     * {@hide}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public ArrayList<String> getStringArrayList(String key) {
        return (ArrayList<String>) mValues.get(key);
    }

    /**
     * Returns a string containing a concise, human-readable description of this object.
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : mValues.keySet()) {
            String value = getAsString(name);
            if (sb.length() > 0) sb.append(" ");
            sb.append(name + "=" + value);
        }
        return sb.toString();
    }
    
    public void log() {
    	Log.d(this + " log: ", toString());
    }
    
    @Override
    public Values clone() throws CloneNotSupportedException {
    	Values clonedValues = new Values(mValues);
    	Set<String> keySet = mValues.keySet();
    	for (String key : keySet) {
    		if((!((mValues.get(key)) instanceof List))) continue;
    		List<Values> valuesList = clonedValues.getAsValuesList(key);
    		if (valuesList != null) {
    			ArrayList<Values> clonedList = new ArrayList<Values>();
    			for (Values v : valuesList) {
    				clonedList.add(v.clone());
				}
    			clonedValues.put(key, clonedList);
    		}
		}
    	return clonedValues;
    }
    
    public void logDiff(Values otherValues) {
    	StringBuilder sb = new StringBuilder();
    	Iterator<String> otherIterator  = otherValues.mValues.keySet().iterator();
    	Iterator<String> iterator = mValues.keySet().iterator();
    	
    	Set<String> otherKeySet = otherValues.mValues.keySet();
    	
        for(int i=0; i < keySet().size(); i++) {
        	String key = iterator.next();
            String value = getAsString(key);
        	if(!otherValues.mValues.containsKey(key)) {
        		sb.append("\n otherValues doesn´t have " + key + ":" + value);
        	}
        	if(value!= null && value.equals(otherValues.mValues.get(key))) {
        		sb.append("\n diff key:" + key + " value:" + value + " otherValue:" + otherValues.mValues.get(key));
        	}

        }
        
        for(int i=0; i < otherKeySet.size(); i++) {
        	String otherKey = otherIterator.next();
            String otherValue = getAsString(otherKey);

        	if(!mValues.containsKey(otherKey)) {
        		sb.append("\n mValues doesn´t have " + otherKey + ":" + otherValue);
        	}
        }
    }

	public void setPhotoSentToServer(String sent) {
		mValues.put(br.fapema.morholt.android.basic.Creator.PHOTO_SENT_TO_SERVER, sent);
	}
	
	public void setSentToServer(String sent) {
		mValues.put(br.fapema.morholt.android.basic.Creator.SENT_TO_SERVER, sent);
	}

}