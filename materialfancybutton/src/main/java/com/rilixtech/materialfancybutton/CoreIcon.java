package com.rilixtech.materialfancybutton;

import com.rilixtech.materialfancybutton.typeface.IIcon;
import com.rilixtech.materialfancybutton.typeface.ITypeface;
import com.rilixtech.materialfancybutton.utils.GenericsUtil;
import com.rilixtech.materialfancybutton.utils.LogHelper;

import java.util.Collection;
import java.util.HashMap;

public final class CoreIcon {

  private static final String TAG = MaterialFancyButton.class.getSimpleName();
  private static final int DOMAIN = 0xD000100;
  private static final LogHelper logHelper = new LogHelper(DOMAIN, TAG);
  private static boolean loggingEnabled = false;

  private static boolean INIT_DONE = false;
  private static final HashMap<String, ITypeface> FONTS = new HashMap<>();



  public static final int FONT_MAPPING_PREFIX = 4;


  private CoreIcon() {
    // Prevent instantiation
  }

  /**
   * initializes the FONTS. This also tries to find all founds automatically via their font file
   */
  public static void init() {
    if (!INIT_DONE) {
      String[] fonts = GenericsUtil.getFields();
      for (String fontsClassPath : fonts) {
        try {
          ITypeface typeface = (ITypeface) Class.forName(fontsClassPath).getDeclaredConstructor().newInstance();
          validateFont(typeface);
          FONTS.put(typeface.getMappingPrefix(), typeface);
          logHelper.logDebug("Typeface = %{public}s", typeface.getAuthor());
        }
        catch (ClassNotFoundException e){
          logHelper.logDebug("%{public}s not found.", fontsClassPath);
        }
        catch (Exception e) {
          logHelper.logDebug("Can't init: %{public}s", fontsClassPath);
        }
      }
      logHelper.logDebug("Total font = %{public}d", FONTS.size());
      INIT_DONE = true;
    }
  }

  /**
   * Test if the icon exists in the currently loaded fonts
   *
   * @param icon The icon to verify
   * @return true if the icon is available
   */
  public static boolean iconExists(String icon) {
    try {
      ITypeface font = findFont(icon.substring(0, FONT_MAPPING_PREFIX));
      icon = icon.replace("-", "_");
      font.getIcon(icon);
      return true;
    } catch (Exception ignore) {
      // Either icon does not exist, or font couldn't be loaded. Return false in this case
    }
    return false;
  }

  /**
   * Registers a fonts into the FONTS array for performance
   */
  public static boolean registerFont(ITypeface font) {
    try {
      validateFont(font);
      FONTS.put(font.getMappingPrefix(), font);
      return true;
    } catch (IllegalArgumentException ex) {
      logHelper.logError("registerFont : Font named %{public}s could not be registered. Exception: %{public}s",
              font.getFontName(), ex.getMessage());
    }
    return false;
  }

  /**
   * Perform a basic sanity check for a font.
   */
  private static void validateFont(ITypeface font) throws IllegalArgumentException{
    final String mappingPrefix = font.getMappingPrefix();
    if (mappingPrefix == null) {
      throw new IllegalArgumentException("The mapping prefix of a font cannot be null.");
    }
    if (mappingPrefix.length() != FONT_MAPPING_PREFIX) {
      throw new IllegalArgumentException("The mapping prefix of a font must be four characters long.");
    }
  }

  /**
   * return all registered FONTS
   */
  public static Collection<ITypeface> getRegisteredFonts() {
    init();
    return FONTS.values();
  }

  /**
   * tries to find a font by its key in all registered FONTS
   */
  public static ITypeface findFont(String key) {
    init();
    return FONTS.get(key);
  }

  /**
   * fetches the font from the Typeface of an IIcon
   */
  public static ITypeface findFont(IIcon icon) {
    return icon.getTypeface();
  }

  public static boolean isLoggingEnabled() {
    return loggingEnabled;
  }

  public static void setLoggingEnabled(boolean loggingEnabled) {
    CoreIcon.loggingEnabled = loggingEnabled;
    logHelper.setEnabled(loggingEnabled);
  }

  public static boolean isInitDone() {
    return INIT_DONE;
  }
}
