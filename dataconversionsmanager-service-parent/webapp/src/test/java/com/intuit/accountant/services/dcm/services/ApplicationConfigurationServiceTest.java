package com.intuit.accountant.services.dcm.services;

import com.intuit.accountant.services.dcm.model.DropDown;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

/**
 * Created by sshashidhar on 19/09/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-test.xml"})
public class ApplicationConfigurationServiceTest {

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    @Value(("${protax.productName}"))
    private String protaxProductName;

    @Value(("${source.name}"))
    private String sourceName;

    @Test
    public void testGetDesktopConfigurationNotNull(){
        Assert.assertTrue(applicationConfigurationService.getDesktopConfiguration()!=null);
    }

    @Test
    public void testGetDesktopCanadaConfigurationNotNull(){
        Assert.assertTrue(applicationConfigurationService.getDesktopConfiguration(protaxProductName)!=null);
    }

    @Test
    public void testGetDesktopProductAndSourceConfigurationNotNull() {
        Assert.assertTrue(applicationConfigurationService.getDesktopConfigurationAsJson(protaxProductName,sourceName)!=null);
    }

    @Test
    public void testGetDesktopProductAndSourceConfigurationResponseNotNull() {
        String response = applicationConfigurationService.getDesktopConfigurationAsJson(protaxProductName,sourceName);
        Assert.assertTrue(!response.isEmpty());
    }

    @Test
    public void testGetDesktopDropDowns(){
        List<DropDown> desktopDropDowns = applicationConfigurationService.getDesktopDropDowns();
        Assert.assertNotNull(desktopDropDowns);
        Assert.assertTrue(desktopDropDowns.size()==2);
        Assert.assertEquals("lacerte", desktopDropDowns.get(0).getCompetitorProduct());
        Assert.assertEquals("taxonline", desktopDropDowns.get(0).getIntuitProduct());
        Assert.assertEquals("2020", desktopDropDowns.get(0).getTaxYearFrom());
        Assert.assertEquals("2021", desktopDropDowns.get(0).getTaxYearTo());
    }

    @Test
    public void testGetDesktopExternalConfigurationNotNull(){
        Assert.assertTrue(applicationConfigurationService.getExternalSystemConfig()!=null);
    }


}
