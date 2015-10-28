/*
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 */

package com.googlecode.psiprobe.tools.logging;

import java.io.File;

/**
 * The Class FileLogAccessor.
 *
 * @author Vlad Ilyushchenko
 * @author Mark Lewis
 */
public class FileLogAccessor extends AbstractLogDestination {

  /** The name. */
  private String name;
  
  /** The file. */
  private File file;

  /* (non-Javadoc)
   * @see com.googlecode.psiprobe.tools.logging.LogDestination#getName()
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /* (non-Javadoc)
   * @see com.googlecode.psiprobe.tools.logging.DefaultAccessor#getTargetClass()
   */
  @Override
  public String getTargetClass() {
    return "stdout";
  }

  /* (non-Javadoc)
   * @see com.googlecode.psiprobe.tools.logging.LogDestination#getLogType()
   */
  public String getLogType() {
    return "stdout";
  }

  /* (non-Javadoc)
   * @see com.googlecode.psiprobe.tools.logging.AbstractLogDestination#getConversionPattern()
   */
  @Override
  public String getConversionPattern() {
    return "";
  }

  /* (non-Javadoc)
   * @see com.googlecode.psiprobe.tools.logging.AbstractLogDestination#getFile()
   */
  @Override
  public File getFile() {
    return file;
  }

  /**
   * Sets the file.
   *
   * @param file the new file
   */
  public void setFile(File file) {
    this.file = file;
  }

}
