package com.zz.msfRpc.msf.util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.MapValue;
import org.msgpack.value.StringValue;
import org.msgpack.value.Value;
import com.zz.msfRpc.config.AuthError;
import com.zz.msfRpc.config.ConnectionError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MsfRpcUtils {

    public static byte[] packOptions(Object... options) throws IOException {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packArrayHeader(options.length);
            for (Object option : options) {
                if (option instanceof String) {
                    packer.packString((String) option);
                } else if (option instanceof Integer) {
                    packer.packInt((Integer) option);
                } else {
                    throw new IllegalArgumentException("Unsupported type: " + option.getClass().getName());
                }
            }
            return packer.toByteArray();
        }
    }

    public static Map<String, Object> sendRequest(String server, int port, String token, byte[] data) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://" + server + ":" + port + "/api");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "binary/message-pack");
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(data);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new ConnectionError("Failed to connect: HTTP error code " + responseCode);
            }

            try (InputStream is = connection.getInputStream()) {
                byte[] responseBytes = is.readAllBytes();
                try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(responseBytes)) {
                    Value result = unpacker.unpackValue();
                    if (result.isMapValue()) {
                        Map<String, Object> response = new HashMap<>();
                        MapValue mapValue = result.asMapValue();
                        for (Map.Entry<Value, Value> entry : mapValue.entrySet()) {
                            Value key = entry.getKey();
                            Value value = entry.getValue();
                            if (key instanceof StringValue && value instanceof StringValue) {
                                response.put(((StringValue) key).asString(), ((StringValue) value).asString());
                            } else {
                                response.put(key.toString(), value.toString());
                            }
                        }
                        return response;
                    } else {
                        throw new AuthError("Invalid response format: " + result);
                    }
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * getConsoleIdFromResponse 方法：
     * 该方法从 response 中提取 consoles 数组。
     * 检查 consoles 是否存在且为 JSONArray 类型。
     * 如果 consoles 数组不为空，获取第一个控制台对象并提取其 id 值。
     * 返回提取的 id 值，如果未找到则返回 null。
     *
     * @param response
     * @return
     */

        public static List<Integer> getConsoleIdFromResponse(Map<String, Object> response) {
            Object consolesObject = response.get("consoles");
            // 检查是否为 JSONArray
            JSONArray jsonArray = convertToJSONArray(consolesObject);
            if (jsonArray != null) {
               List<String>  consoleIdsList = getConsoleIdFromJsonArray(jsonArray);
                if (consoleIdsList != null && consoleIdsList.size()>0) {
                    return consoleIdsList.stream()
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                }
            } else {
                System.out.println("Response does not contain a valid JSONArray for 'consoles'");
            }

            return null;
        }

    public static JSONArray convertToJSONArray(Object obj) {
        if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            return new JSONArray(list);
        } else if (obj instanceof String) {
            try {
                return new JSONArray((String) obj);
            } catch (JSONException e) {
                System.out.println("Failed to parse string to JSONArray: " + e.getMessage());
            }
        }
        return null;
    }

    public static List<String> getConsoleIdFromJsonArray(JSONArray consoles) {
            List<String> ids = new ArrayList<>();
            if (consoles != null && consoles.length() > 0) {
                for (int i = 0; i < consoles.length(); i++) {
                    JSONObject console = consoles.getJSONObject(i);
                    if (console.has("id")) {
                        String id = console.getString("id");
                        ids.add(id);
                    }
                }
            }
        return ids;
    }



}