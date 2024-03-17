/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.init;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Daniel
 */
public class SaveResource extends Util {
    protected File extractfolder = new File(plugin.getDataFolder() + File.separator + "html");
    protected String folderpath = "html";

    public SaveResource(WebPortal plugin) {
        super(plugin);
    }

    public void extract() throws IOException {
        File jarfile = null;


        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);

            jarfile = (File) method.invoke(this.plugin);
        } catch (Exception e) {
            throw new IOException(e);
        }

        if (!this.extractfolder.exists()) {
            this.extractfolder.mkdirs();
        }

        JarFile jar = new JarFile(jarfile);

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String path = entry.getName();


            if (!path.startsWith(this.folderpath)) {
                continue;
            }

            if (entry.isDirectory()) {
                File file = new File(this.extractfolder, entry.getName().replaceFirst(this.folderpath, ""));

                if (!file.exists()) {
                    file.mkdirs();
                }
            } else {
                File file = new File(this.extractfolder, path.replaceFirst(this.folderpath, ""));
                if (!file.exists()) {
                    InputStream is = jar.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(file);

                    while (is.available() > 0) {
                        fos.write(is.read());
                    }

                    fos.close();
                    is.close();
                }
            }
        }

        jar.close();
    }
}
