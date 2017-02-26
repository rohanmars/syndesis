/**
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.ipaas.jsondb;

import java.io.*;
import java.util.function.Consumer;

/**
 * Provides a Java API to read/update a Key/Value database presented to the user a
 * single large persistent JSON tree/document.  You can read or update subsets of the
 * the document by specifying the path to the nodes in the JSON document that your
 * interested in working with.
 */
public interface JsonDB {

    /**
     * @param path to the json object or value to delete
     * @return true if the object or value existed and was deleted.
     */
    boolean delete(String path);

    /**
     * @param path to the json object or value to check if it exists
     * @return true if the object or value exists.
     */
    boolean exists(String path);

    /**
     * Generates a sortable unique id as described at:
     * https://firebase.googleblog.com/2015/02/the-2120-ways-to-ensure-unique_68.html
     *
     * @return a sortable unique id
     */
    String createKey();

    ///////////////////////////////////////////////////////////////////
    //
    // Convenience Methods for working with JSON as Strings
    //
    ///////////////////////////////////////////////////////////////////

    /**
     * Same as {@code  getAsString(path, null)}
     */
    default String getAsString(String path) {
        return getAsString(path, null);
    }

    /**
     * @param path - the object or value to get
     * @param options options that control formatting of the result.
     * @return the json result or null if the path does not exist
     */
    default String getAsString(String path, GetOptions options)  {
        try {
            byte[] data = getAsByteArray(path, options);
            if( data==null ) {
                return null;
            }
            return new String(data, "UTF-8");
        } catch (IOException e) {
            throw new JsonDBException(e);
        }
    }

    /**
     * Creates or Replaces the object or value at the given path
     * with the supplied json document.
     *
     * @param path to the object or value to set
     * @param json value to set it to, can be a json primitive or struct
     */
    default void set(String path, String json)  {
        try {
            set(path, json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new JsonDBException(e);
        }
    }

    /**
     * Pushes a new value to the the requested path.
     * Same as: {@code set( path + "/" + createKey(), json)}
     *
     * @param path to the object or value to set
     * @param json value to set it to, can be a json primitive or struct
     * @return the field name that was added to the path object
     */
    default String push(String path, String json) {
        try {
            return push(path, json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new JsonDBException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////
    //
    // Convenience Methods for working with JSON as byte arrays
    //
    ///////////////////////////////////////////////////////////////////

    default byte[] getAsByteArray(String path) {
        return getAsByteArray(path, null);
    }

    default byte[] getAsByteArray(String path, GetOptions options) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if( !getAsStream(path, options, os) ) {
            return null;
        }
        return os.toByteArray();
    }

    default void set(String path, byte[] json) {
        set(path, new ByteArrayInputStream(json));
    }

    default String push(String path, byte[] json) {
        return push(path, new ByteArrayInputStream(json));
    }

    ///////////////////////////////////////////////////////////////////
    //
    // Methods for working with JSON as Streams.. These methods provide
    // the most memory efficient style to work the database since you
    // don't need to hold the entire input or output in memory.
    //
    ///////////////////////////////////////////////////////////////////
    default void getAsStream(String path, OutputStream os) {
        getAsStream(path, null, os);
    }

    default boolean getAsStream(String path, GetOptions options, OutputStream os) {
        Consumer<OutputStream> so = getAsStreamingOutput(path, options);
        if( so!=null ) {
            so.accept(os);
            return true;
        }
        return false;
    }

    default Consumer<OutputStream> getAsStreamingOutput(String path) {
        return getAsStreamingOutput(path, null);
    }

    Consumer<OutputStream> getAsStreamingOutput(String path, GetOptions options);

    void set(String path, InputStream body);

    String push(String path, InputStream body);

}
