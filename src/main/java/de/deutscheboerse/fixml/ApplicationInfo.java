package de.deutscheboerse.fixml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public final class ApplicationInfo
{
    public static final String VERSION_FILE = "doc/version.txt";
    public static final String UNKNOWN = "[UNKNOWN]";
    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInfo.class);

    private ApplicationInfo()
    {
        throw new IllegalStateException("Utility class");
    }

    public static String getVersion()
    {
        return getValueFromVersionFile("version");
    }

    public static String getTimestamp()
    {
        return getValueFromVersionFile("timestamp");
    }

    public static String getSupportedTlsVersions()
    {
        try
        {
            return String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols());
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.warn("Failed to detect supported TLS versions.");
            return UNKNOWN;
        }
    }

    public static String getAddress()
    {
        try
        {
            return Inet4Address.getLocalHost().toString();
        }
        catch (UnknownHostException e)
        {
            LOGGER.warn("Failed to get IP address.");
            return UNKNOWN;
        }
    }

    public static String getDependencies()
    {
        final StringBuilder sb = new StringBuilder();
        try
        {
            Files.walk(getApplicationRoot().resolve("lib"))
                    .filter(file -> file.toString().endsWith(".jar"))
                    .map(Path::getFileName)
                    .sorted()
                    .forEach(jar -> sb.append("\n - ").append(jar));
        }
        catch (URISyntaxException | IOException e)
        {
            LOGGER.warn("Failed to get dependencies from lib directory. " + e);
        }
        return sb.toString();
    }

    private static String getValueFromVersionFile(String value)
    {
        try
        {
            return Files.lines(getApplicationRoot().resolve(VERSION_FILE))
                    .filter(line -> line.startsWith(value + "="))
                    .map(line -> line.substring(line.lastIndexOf("=") + 1))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
        catch (URISyntaxException | IOException e)
        {
            LOGGER.warn("Failed to get " + value + " from version file: '" + VERSION_FILE + "'. " + e);
            return UNKNOWN;
        }
    }

    private static Path getApplicationRoot() throws URISyntaxException
    {
        final Path path = new File(ApplicationInfo.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath();
        final Path parent = path.getParent();
        if (parent != null)
        {
            return parent;
        }
        throw new URISyntaxException("Source location is invalid", "Unable to determine application root");
    }
}
