package org.datacleaner.result.html

import org.junit.{Assert, Test}
import org.scalatestplus.junit.AssertionsForJUnit

class FlotChartLocatorTest extends AssertionsForJUnit {

  @Test
  def testBasicStuff = {
    Assert.assertEquals("http://cdnjs.cloudflare.com/ajax/libs/flot/0.8.3/jquery.flot.min.js", FlotChartLocator.getFlotBaseUrl);
    
    FlotChartLocator.setFlotHome("../bar/");
    
    Assert.assertEquals("../bar/jquery.flot.min.js", FlotChartLocator.getFlotBaseUrl);
    
    FlotChartLocator.setFlotHome(null)
    
    Assert.assertEquals("http://cdnjs.cloudflare.com/ajax/libs/flot/0.8.3/jquery.flot.min.js", FlotChartLocator.getFlotBaseUrl);
  }
}
