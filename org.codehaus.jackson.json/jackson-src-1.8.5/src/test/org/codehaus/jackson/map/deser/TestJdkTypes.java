package org.codehaus.jackson.map.deser;

import java.io.*;
import java.net.*;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.*;

public class TestJdkTypes
    extends org.codehaus.jackson.map.BaseMapTest
{
    static class PrimitivesBean
    {
        public boolean booleanValue = true;
        public byte byteValue = 3;
        public char charValue = 'a';
        public short shortValue = 37;
        public int intValue = 1;
        public long longValue = 100L;
        public float floatValue = 0.25f;
        public double doubleValue = -1.0;
    }

    static class ParamClassBean
    {
         public String name = "bar";
         public Class<String> clazz ;

         public ParamClassBean() { }
         public ParamClassBean(String name) {
             this.name = name;
             clazz = String.class;
         }
    }    

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    /**
     * Related to issue [JACKSON-155].
     */
    public void testFile() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        // Not portable etc... has to do:
        File src = new File("/test").getAbsoluteFile();
        File result = m.readValue("\""+src.getAbsolutePath()+"\"", File.class);
        assertEquals(src.getAbsolutePath(), result.getAbsolutePath());
    }

    public void testRegexps() throws IOException
    {
        final String PATTERN_STR = "abc:\\s?(\\d+)";
        Pattern exp = Pattern.compile(PATTERN_STR);
        /* Ok: easiest way is to just serialize first; problem
         * is the backslash
         */
        ObjectMapper m = new ObjectMapper();
        String json = m.writeValueAsString(exp);
        Pattern result = m.readValue(json, Pattern.class);
        assertEquals(exp.pattern(), result.pattern());
    }

    public void testCurrency() throws IOException
    {
        Currency usd = Currency.getInstance("USD");
        assertEquals(usd, new ObjectMapper().readValue(quote("USD"), Currency.class));
    }

    /**
     * Test for [JACKSON-419]
     * 
     * @since 1.7
     */
    public void testLocale() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(new Locale("en"), mapper.readValue(quote("en"), Locale.class));
        assertEquals(new Locale("es", "ES"), mapper.readValue(quote("es_ES"), Locale.class));
        assertEquals(new Locale("FI", "fi", "savo"), mapper.readValue(quote("fi_FI_savo"), Locale.class));
    }

    /**
     * Test for [JACKSON-420] (add DeserializationConfig.FAIL_ON_NULL_FOR_PRIMITIVES)
     * 
     * @since 1.7
     */
    public void testNullForPrimitives() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        // by default, ok to rely on defaults
        PrimitivesBean bean = mapper.readValue("{\"intValue\":null, \"booleanValue\":null, \"doubleValue\":null}",
                PrimitivesBean.class);
        assertNotNull(bean);
        assertEquals(0, bean.intValue);
        assertEquals(false, bean.booleanValue);
        assertEquals(0.0, bean.doubleValue);

        bean = mapper.readValue("{\"byteValue\":null, \"longValue\":null, \"floatValue\":null}",
                PrimitivesBean.class);
        assertNotNull(bean);
        assertEquals((byte) 0, bean.byteValue);
        assertEquals(0L, bean.longValue);
        assertEquals(0.0f, bean.floatValue);
        
        // but not when enabled
        mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, true);

        // boolean
        try {
            mapper.readValue("{\"booleanValue\":null}", PrimitivesBean.class);
            fail("Expected failure for boolean + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type boolean");
        }
        // byte/char/short/int/long
        try {
            mapper.readValue("{\"byteValue\":null}", PrimitivesBean.class);
            fail("Expected failure for byte + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type byte");
        }
        try {
            mapper.readValue("{\"charValue\":null}", PrimitivesBean.class);
            fail("Expected failure for char + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type char");
        }
        try {
            mapper.readValue("{\"shortValue\":null}", PrimitivesBean.class);
            fail("Expected failure for short + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type short");
        }
        try {
            mapper.readValue("{\"intValue\":null}", PrimitivesBean.class);
            fail("Expected failure for int + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type int");
        }
        try {
            mapper.readValue("{\"longValue\":null}", PrimitivesBean.class);
            fail("Expected failure for long + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type long");
        }

        // float/double
        try {
            mapper.readValue("{\"floatValue\":null}", PrimitivesBean.class);
            fail("Expected failure for float + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type float");
        }
        try {
            mapper.readValue("{\"doubleValue\":null}", PrimitivesBean.class);
            fail("Expected failure for double + null");
        } catch (JsonMappingException e) {
            verifyException(e, "Can not map JSON null into type double");
        }
    }
 
    /**
     * Test for [JACKSON-483], allow handling of CharSequence
     */
    public void testCharSequence() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        CharSequence cs = mapper.readValue("\"abc\"", CharSequence.class);
        assertEquals(String.class, cs.getClass());
        assertEquals("abc", cs.toString());
    }
    
    // [JACKSON-484]
    public void testInetAddress() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        InetAddress address = mapper.readValue(quote("127.0.0.1"), InetAddress.class);
        assertEquals("127.0.0.1", address.getHostAddress());

        // should we try resolving host names? That requires connectivity... 
        final String HOST = "www.ning.com";
        address = mapper.readValue(quote(HOST), InetAddress.class);
        assertEquals(HOST, address.getHostName());
    }

    // [JACKSON-597]
    public void testClass() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        assertSame(String.class, mapper.readValue(quote("java.lang.String"), Class.class));

        // then primitive types
        assertSame(Boolean.TYPE, mapper.readValue(quote("boolean"), Class.class));
        assertSame(Byte.TYPE, mapper.readValue(quote("byte"), Class.class));
        assertSame(Short.TYPE, mapper.readValue(quote("short"), Class.class));
        assertSame(Character.TYPE, mapper.readValue(quote("char"), Class.class));
        assertSame(Integer.TYPE, mapper.readValue(quote("int"), Class.class));
        assertSame(Long.TYPE, mapper.readValue(quote("long"), Class.class));
        assertSame(Float.TYPE, mapper.readValue(quote("float"), Class.class));
        assertSame(Double.TYPE, mapper.readValue(quote("double"), Class.class));
        assertSame(Void.TYPE, mapper.readValue(quote("void"), Class.class));
    }

    // [JACKSON-605]
    public void testClassWithParams() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new ParamClassBean("Foobar"));

        ParamClassBean result = mapper.readValue(json, ParamClassBean.class);
        assertEquals("Foobar", result.name);
        assertSame(String.class, result.clazz);
    }
}
