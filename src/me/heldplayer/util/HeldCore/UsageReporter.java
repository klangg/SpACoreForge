
package me.heldplayer.util.HeldCore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import cpw.mods.fml.relauncher.Side;

/**
 * Class that relays usage statistics to the server located at
 * http://dsiwars.x10.mx/
 * 
 * @author heldplayer
 * 
 */
public class UsageReporter implements Runnable {

    public final String modId;
    public final String version;
    public final Side side;
    public final File modDir;

    public UsageReporter(String modId, String version, Side side, File modDir) {
        this.modId = modId;
        this.version = version;
        this.side = side;
        this.modDir = modDir;
    }

    @Override
    public void run() {
        HttpURLConnection request = null;

        boolean needsResending = false;

        try {
            needsResending = this.needsResending();
        }
        catch (RuntimeException e) {
            return;
        }
        if (needsResending) {
            try {
                request = (HttpURLConnection) new URL("http://dsiwars.x10.mx/files/report_activation.php?mod=" + this.modId + "&version=" + this.version + "&side=" + side.ordinal()).openConnection();
                request.setRequestMethod("GET");
                request.connect();

                if (request.getResponseCode() == 200) {
                    rewriteVersion();
                }
                else {
                    throw new RuntimeException("Server returned HTTP response code " + request.getResponseCode());
                }
            }
            catch (MalformedURLException e) {
                Updater.log.log(Level.SEVERE, "Failed reporting activation", e);
            }
            catch (IOException e) {
                Updater.log.log(Level.SEVERE, "Failed reporting activation", e);
            }
            catch (RuntimeException e) {
                Updater.log.log(Level.SEVERE, "Failed reporting activation: " + e.getMessage());
            }
            finally {
                if (request != null) {
                    request.disconnect();
                }
            }
        }

        request = null;

        try {
            request = (HttpURLConnection) new URL("http://dsiwars.x10.mx/files/report_launch.php?mod=" + this.modId + "&version=" + this.version + "&side=" + side.ordinal()).openConnection();
            request.setRequestMethod("GET");
            request.connect();

            if (request.getResponseCode() != 200) {
                throw new RuntimeException("Server returned HTTP response code " + request.getResponseCode());
            }
        }
        catch (Exception e) {
            Updater.log.log(Level.SEVERE, "Failed reporting activation: " + e.getMessage());
        }
        finally {
            if (request != null) {
                request.disconnect();
            }
        }
    }

    private void rewriteVersion() {
        File file = new File(modDir, this.modId + ".version");

        BufferedWriter out = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            out = new BufferedWriter(new FileWriter(file));

            out.write(this.version);
            out.write("\r\n");
            out.write(file.getAbsolutePath());
            out.write("\r\n");
            out.write("# Please do not edit this file, nor ship it with any modpack, it is used to determine how many people activate this mod");
        }
        catch (IOException e) {}
        finally {
            try {
                out.close();
            }
            catch (IOException e) {}
        }
    }

    private boolean needsResending() {
        File file = new File(modDir, this.modId + ".version");

        if (!file.exists()) {
            return true;
        }

        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(file));

            String version = in.readLine();

            if (version == null || version.isEmpty()) {
                return true;
            }
            if (version.equals("DEBUG")) {
                throw new RuntimeException("Running in dev environment");
            }
            if (!version.equals(this.version)) {
                return true;
            }

            String path = in.readLine();

            if (path == null || path.isEmpty()) {
                return true;
            }
            if (!path.equals(file.getAbsolutePath())) {
                return true;
            }

            return false;
        }
        catch (IOException e) {
            return true;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {}
        }
    }

}
