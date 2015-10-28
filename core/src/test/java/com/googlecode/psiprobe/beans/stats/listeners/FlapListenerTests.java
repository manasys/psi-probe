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
package com.googlecode.psiprobe.beans.stats.listeners;

import org.junit.Assert;
import org.junit.Test;

/**
 * The Class FlapListenerTests.
 *
 * @author Mark Lewis
 */
public class FlapListenerTests {

  /** The default threshold. */
  private final int defaultThreshold = 10;
  
  /** The default interval. */
  private final int defaultInterval = 10;
  
  /** The default start threshold. */
  private final float defaultStartThreshold = 0.29f;
  
  /** The default stop threshold. */
  private final float defaultStopThreshold = 0.49f;
  
  /** The default low weight. */
  private final float defaultLowWeight = 1.0f;
  
  /** The default high weight. */
  private final float defaultHighWeight = 1.0f;

  /** The listener. */
  private MockFlapListener listener = new MockFlapListener(defaultThreshold, defaultInterval,
      defaultStartThreshold, defaultStopThreshold, defaultLowWeight, defaultHighWeight);

  /** The below threshold. */
  private StatsCollectionEvent belowThreshold = new StatsCollectionEvent("test", 0, 0);
  
  /** The above threshold. */
  private StatsCollectionEvent aboveThreshold = new StatsCollectionEvent("test", 0, 20);

  /**
   * Fill.
   *
   * @param sce the sce
   */
  protected void fill(StatsCollectionEvent sce) {
    listener.reset();
    add(sce, defaultInterval);
  }

  /**
   * Adds the.
   *
   * @param sce the sce
   * @param quantity the quantity
   */
  protected void add(StatsCollectionEvent sce, int quantity) {
    for (int i = 0; i < quantity; i++) {
      listener.statsCollected(sce);
    }
  }

  /**
   * Test below threshold not flapping.
   */
  @Test
  public void testBelowThresholdNotFlapping() {
    listener.reset();
    listener.statsCollected(aboveThreshold);
    listener.statsCollected(belowThreshold);
    Assert.assertTrue(listener.isBelowThresholdNotFlapping());
  }

  /**
   * Test above threshold not flapping.
   */
  @Test
  public void testAboveThresholdNotFlapping() {
    listener.reset();
    listener.statsCollected(belowThreshold);
    listener.statsCollected(aboveThreshold);
    Assert.assertTrue(listener.isAboveThresholdNotFlapping());
  }

  /**
   * Test still below threshold.
   */
  @Test
  public void testStillBelowThreshold() {
    listener.reset();
    listener.statsCollected(belowThreshold);
    for (int i = 0; i < defaultInterval; i++) {
      listener.statsCollected(belowThreshold);
      Assert.assertFalse(listener.isBelowThresholdNotFlapping());
    }
  }

  /**
   * Test still above threshold.
   */
  @Test
  public void testStillAboveThreshold() {
    listener.reset();
    listener.statsCollected(aboveThreshold);
    for (int i = 0; i < defaultInterval; i++) {
      listener.statsCollected(aboveThreshold);
      Assert.assertFalse(listener.isAboveThresholdNotFlapping());
    }
  }

  /**
   * Test flapping started.
   */
  @Test
  public void testFlappingStarted() {
    fill(belowThreshold);
    listener.statsCollected(aboveThreshold);
    listener.statsCollected(belowThreshold);
    listener.statsCollected(aboveThreshold);
    Assert.assertTrue(listener.isFlappingStarted());
  }

  /**
   * Test flapping started2.
   */
  @Test
  public void testFlappingStarted2() {
    fill(aboveThreshold);
    listener.statsCollected(belowThreshold);
    listener.statsCollected(aboveThreshold);
    listener.statsCollected(belowThreshold);
    Assert.assertTrue(listener.isFlappingStarted());
  }

  /**
   * Test below threshold flapping stopped below.
   */
  @Test
  public void testBelowThresholdFlappingStoppedBelow() {
    fill(belowThreshold);
    listener.statsCollected(aboveThreshold);
    listener.statsCollected(belowThreshold);
    listener.statsCollected(aboveThreshold);
    Assert.assertTrue(listener.isFlappingStarted());
    add(belowThreshold, 5);
    Assert.assertTrue(listener.isBelowThresholdFlappingStopped());
  }

  /**
   * Test below threshold flapping stopped above.
   */
  @Test
  public void testBelowThresholdFlappingStoppedAbove() {
    fill(belowThreshold);
    listener.statsCollected(aboveThreshold);
    listener.statsCollected(belowThreshold);
    listener.statsCollected(aboveThreshold);
    Assert.assertTrue(listener.isFlappingStarted());
    add(aboveThreshold, 5);
    Assert.assertTrue(listener.isAboveThresholdFlappingStopped());
  }

  /**
   * Test above threshold flapping stopped below.
   */
  @Test
  public void testAboveThresholdFlappingStoppedBelow() {
    fill(aboveThreshold);
    listener.statsCollected(belowThreshold);
    listener.statsCollected(aboveThreshold);
    listener.statsCollected(belowThreshold);
    Assert.assertTrue(listener.isFlappingStarted());
    add(belowThreshold, 5);
    Assert.assertTrue(listener.isBelowThresholdFlappingStopped());
  }

  /**
   * Test above threshold flapping stopped above.
   */
  @Test
  public void testAboveThresholdFlappingStoppedAbove() {
    fill(aboveThreshold);
    listener.statsCollected(belowThreshold);
    listener.statsCollected(aboveThreshold);
    listener.statsCollected(belowThreshold);
    Assert.assertTrue(listener.isFlappingStarted());
    add(aboveThreshold, 5);
    Assert.assertTrue(listener.isAboveThresholdFlappingStopped());
  }

  /**
   * The listener interface for receiving mockFlap events.
   * The class that is interested in processing a mockFlap
   * event implements this interface, and the object created
   * with that class is registered with a component using the
   * component's <code>addMockFlapListener<code> method. When
   * the mockFlap event occurs, that object's appropriate
   * method is invoked.
   *
   * @see MockFlapEvent
   */
  public static class MockFlapListener extends FlapListener {

    /** The threshold. */
    private final long threshold;

    /** The flapping started. */
    private boolean flappingStarted;
    
    /** The above threshold flapping stopped. */
    private boolean aboveThresholdFlappingStopped;
    
    /** The below threshold flapping stopped. */
    private boolean belowThresholdFlappingStopped;
    
    /** The above threshold not flapping. */
    private boolean aboveThresholdNotFlapping;
    
    /** The below threshold not flapping. */
    private boolean belowThresholdNotFlapping;

    /**
     * Instantiates a new mock flap listener.
     *
     * @param threshold the threshold
     * @param flapInterval the flap interval
     * @param flapStartThreshold the flap start threshold
     * @param flapStopThreshold the flap stop threshold
     * @param lowWeight the low weight
     * @param highWeight the high weight
     */
    public MockFlapListener(long threshold, int flapInterval, float flapStartThreshold,
        float flapStopThreshold, float lowWeight, float highWeight) {

      this.threshold = threshold;
      setDefaultFlapInterval(flapInterval);
      setDefaultFlapStartThreshold(flapStartThreshold);
      setDefaultFlapStopThreshold(flapStopThreshold);
      setDefaultFlapLowWeight(lowWeight);
      setDefaultFlapHighWeight(highWeight);
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.ThresholdListener#statsCollected(com.googlecode.psiprobe.beans.stats.listeners.StatsCollectionEvent)
     */
    @Override
    public void statsCollected(StatsCollectionEvent sce) {
      resetFlags();
      super.statsCollected(sce);
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.FlapListener#flappingStarted(com.googlecode.psiprobe.beans.stats.listeners.StatsCollectionEvent)
     */
    protected void flappingStarted(StatsCollectionEvent sce) {
      flappingStarted = true;
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.FlapListener#aboveThresholdFlappingStopped(com.googlecode.psiprobe.beans.stats.listeners.StatsCollectionEvent)
     */
    protected void aboveThresholdFlappingStopped(StatsCollectionEvent sce) {
      aboveThresholdFlappingStopped = true;
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.FlapListener#belowThresholdFlappingStopped(com.googlecode.psiprobe.beans.stats.listeners.StatsCollectionEvent)
     */
    protected void belowThresholdFlappingStopped(StatsCollectionEvent sce) {
      belowThresholdFlappingStopped = true;
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.FlapListener#aboveThresholdNotFlapping(com.googlecode.psiprobe.beans.stats.listeners.StatsCollectionEvent)
     */
    protected void aboveThresholdNotFlapping(StatsCollectionEvent sce) {
      aboveThresholdNotFlapping = true;
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.FlapListener#belowThresholdNotFlapping(com.googlecode.psiprobe.beans.stats.listeners.StatsCollectionEvent)
     */
    protected void belowThresholdNotFlapping(StatsCollectionEvent sce) {
      belowThresholdNotFlapping = true;
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.ThresholdListener#getThreshold(java.lang.String)
     */
    @Override
    public long getThreshold(String name) {
      return threshold;
    }

    /* (non-Javadoc)
     * @see com.googlecode.psiprobe.beans.stats.listeners.FlapListener#reset()
     */
    @Override
    public void reset() {
      resetFlags();
      super.reset();
    }

    /**
     * Reset flags.
     */
    public void resetFlags() {
      flappingStarted = false;
      aboveThresholdFlappingStopped = false;
      belowThresholdFlappingStopped = false;
      aboveThresholdNotFlapping = false;
      belowThresholdNotFlapping = false;
    }

    /**
     * Checks if is above threshold flapping stopped.
     *
     * @return true, if is above threshold flapping stopped
     */
    public boolean isAboveThresholdFlappingStopped() {
      return aboveThresholdFlappingStopped;
    }

    /**
     * Checks if is above threshold not flapping.
     *
     * @return true, if is above threshold not flapping
     */
    public boolean isAboveThresholdNotFlapping() {
      return aboveThresholdNotFlapping;
    }

    /**
     * Checks if is below threshold flapping stopped.
     *
     * @return true, if is below threshold flapping stopped
     */
    public boolean isBelowThresholdFlappingStopped() {
      return belowThresholdFlappingStopped;
    }

    /**
     * Checks if is below threshold not flapping.
     *
     * @return true, if is below threshold not flapping
     */
    public boolean isBelowThresholdNotFlapping() {
      return belowThresholdNotFlapping;
    }

    /**
     * Checks if is flapping started.
     *
     * @return true, if is flapping started
     */
    public boolean isFlappingStarted() {
      return flappingStarted;
    }

  }

}
