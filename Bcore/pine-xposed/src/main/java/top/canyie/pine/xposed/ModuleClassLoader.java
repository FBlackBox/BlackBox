/*
 * Copyright (C) 2017 The Android Open Source Project
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
 * limitations under the License
 */

// Pine changed: Move package to top.canyie.pine.xposed
package top.canyie.pine.xposed;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.PathClassLoader;

// Pine changed: Rename to ModuleClassLoader
public class ModuleClassLoader extends PathClassLoader {
    public ModuleClassLoader(String dexPath, ClassLoader parent) {
        super(dexPath, parent);
    }

    public ModuleClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, librarySearchPath, parent);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First, check whether the class has already been loaded. Return it if that's the case.
        Class<?> cl = findLoadedClass(name);
        if (cl != null) {
            return cl;
        }

        // Next, check whether the class in question is present in the boot classpath.
        try {
            return Object.class.getClassLoader().loadClass(name);
        } catch (ClassNotFoundException ignored) {
        }

        // Next, check whether the class in question is present in the dexPath that this classloader
        // operates on, or its shared libraries.
        ClassNotFoundException fromSuper;
        try {
            return findClass(name);
        } catch (ClassNotFoundException ex) {
            fromSuper = ex;
        }

        // Finally, check whether the class in question is present in the parent classloader.
        try {
            return getParent().loadClass(name);
        } catch (ClassNotFoundException cnfe) {
            // The exception we're catching here is the CNFE thrown by the parent of this
            // classloader. However, we would like to throw a CNFE that provides details about
            // the class path / list of dex files associated with *this* classloader, so we choose
            // to throw the exception thrown from that lookup.
            throw fromSuper;
        }
    }

    // Pine added: loadClassNoDelegate
    public Class<?> loadClassNoDelegate(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    // Pine added: public findClass
    @Override public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    public URL getResource(String name) {
        // The lookup order we use here is the same as for classes.

        URL resource = Object.class.getClassLoader().getResource(name);
        if (resource != null) {
            return resource;
        }

        resource = findResource(name);
        if (resource != null) {
            return resource;
        }

        final ClassLoader cl = getParent();
        return (cl == null) ? null : cl.getResource(name);
    }

    // Pine added: getResourceNoDelegate
    public URL getResourceNoDelegate(String name) {
        return super.getResource(name);
    }

    // Pine added: public findResource
    @Override public URL findResource(String name) {
        return super.findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        @SuppressWarnings("unchecked")
        final Enumeration<URL>[] resources = (Enumeration<URL>[]) new Enumeration<?>[] {
                Object.class.getClassLoader().getResources(name),
                findResources(name),
                (getParent() == null)
                        ? null : getParent().getResources(name) };

        return new CompoundEnumeration<>(resources);
    }

    // Pine added: getResourcesNoDelegate
    public Enumeration<URL> getResourcesNoDelegate(String name) throws IOException {
        return super.getResources(name);
    }
}
