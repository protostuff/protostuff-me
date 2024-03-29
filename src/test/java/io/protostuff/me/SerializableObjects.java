//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package io.protostuff.me;

import junit.framework.Assert;

import io.protostuff.me.Foo.EnumSample;

/**
 * The objects to be tested.
 * 
 * @author David Yu
 * @created Nov 13, 2009
 */
public final class SerializableObjects
{

    public static final Baz negativeBaz = newBaz(-567, "negativeBaz", -202020202);
    public static final Bar negativeBar = newBar(-12, "negativeBar", negativeBaz, Bar.Status.STARTED,
            ByteString.copyFromUtf8("a1"), Boolean.TRUE, -130.031f, -1000.0001d, -101010101);

    public static final Baz baz = newBaz(567, "baz", 202020202);
    public static final Bar bar = newBar(890, "bar", baz, Bar.Status.STARTED,
            ByteString.copyFromUtf8("b2"), Boolean.TRUE, 150.051f, 2000.0002d, 303030303);

    public static final Foo foo = newFoo(
            new Integer[] { 90210, -90210, 0 },
            new String[] { "ab", "cd" },
            new Bar[] { bar, negativeBar },
            new int[] { EnumSample.TYPE0, EnumSample.TYPE2 },
            new ByteString[] { ByteString.copyFromUtf8("ef"), ByteString.copyFromUtf8("gh") },
            new Boolean[] { Boolean.TRUE, Boolean.FALSE },
            new Float[] { 1234.4321f, -1234.4321f, 0f },
            new Double[] { 12345678.87654321d, -12345678.87654321d, 0d },
            new Long[] { 7060504030201l, -7060504030201l, 0l });

    public static Baz newBaz(int id, String name, long timestamp)
    {
        Baz baz = new Baz();

        baz.setId(id);
        baz.setName(name);
        baz.setTimestamp(timestamp);

        return baz;
    }

    public static Bar newBar(int someInt, String someString,
            Baz someBaz, int someEnum, ByteString someBytes,
            Boolean someBoolean, float someFloat, double someDouble, long someLong)
    {
        Bar bar = new Bar();

        bar.setSomeInt(someInt);
        bar.setSomeString(someString);
        bar.setSomeBaz(someBaz);
        bar.setSomeEnum(someEnum);
        bar.setSomeBytes(someBytes);
        bar.setSomeBoolean(someBoolean);
        bar.setSomeFloat(someFloat);
        bar.setSomeDouble(someDouble);
        bar.setSomeLong(someLong);

        return bar;
    }

    public static Foo newFoo(
            Integer[] someInt,
            String[] someString,
            Bar[] someBar,
            int[] someEnum,
            ByteString[] someBytes,
            Boolean[] someBoolean,
            Float[] someFloat,
            Double[] someDouble,
            Long[] someLong)
    {

        Foo foo = new Foo();

        for (int i = 0; i < someInt.length; i++)
            foo.addSomeInt(someInt[i]);

        for (int i = 0; i < someString.length; i++)
            foo.addSomeString(someString[i]);

        for (int i = 0; i < someBar.length; i++)
            foo.addSomeBar(someBar[i]);

        for (int i = 0; i < someEnum.length; i++)
            foo.addSomeEnum(new Integer(someEnum[i]));

        for (int i = 0; i < someBytes.length; i++)
            foo.addSomeBytes(someBytes[i]);

        for (int i = 0; i < someBoolean.length; i++)
            foo.addSomeBoolean(someBoolean[i]);

        for (int i = 0; i < someFloat.length; i++)
            foo.addSomeFloat(someFloat[i]);

        for (int i = 0; i < someDouble.length; i++)
            foo.addSomeDouble(someDouble[i]);

        for (int i = 0; i < someLong.length; i++)
            foo.addSomeLong(someLong[i]);

        return foo;
        /*
         * return new Foo( someInt == null ? null : Arrays.asList(someInt), someString == null ? null :
         * Arrays.asList(someString), someBar == null ? null : Arrays.asList(someBar), someEnum == null ? null :
         * Arrays.asList(someEnum), someBytes == null ? null : Arrays.asList(someBytes), someBoolean == null ? null :
         * Arrays.asList(someBoolean), someFloat == null ? null : Arrays.asList(someFloat), someDouble == null ? null :
         * Arrays.asList(someDouble), someLong == null ? null : Arrays.asList(someLong));
         */
    }

    public static <T> void assertEquals(T m1, T m2)
    {
        Assert.assertEquals(m1, m2);
    }

    /*
     * public static void assertEquals(Baz baz1, Baz baz2) { // true if both are null if(baz1 == baz2) return;
     * 
     * Assert.assertTrue(baz1.getId() == baz2.getId()); Assert.assertEquals(baz1.getName(), baz2.getName());
     * Assert.assertTrue(baz1.getTimestamp() == baz2.getTimestamp()); }
     * 
     * public static void assertEquals(Bar bar1, Bar bar2) { // true if both are null if(bar1 == bar2) return;
     * 
     * Assert.assertTrue(bar1.getSomeInt() == bar2.getSomeInt()); Assert.assertEquals(bar1.getSomeString(),
     * bar2.getSomeString()); assertEquals(bar1.getBaz(), bar2.getBaz()); Assert.assertTrue(bar1.getSomeEnum() ==
     * bar2.getSomeEnum()); Assert.assertEquals(bar1.getSomeBytes(), bar2.getSomeBytes());
     * Assert.assertTrue(bar1.getSomeBoolean() == bar2.getSomeBoolean()); Assert.assertTrue(bar1.getSomeFloat() ==
     * bar2.getSomeFloat()); Assert.assertTrue(bar1.getSomeDouble() == bar2.getSomeDouble());
     * Assert.assertTrue(bar1.getSomeLong() == bar2.getSomeLong()); }
     * 
     * public static void assertEquals(Foo f1, Foo f2) { // true if both are null if(f1 == f2) return;
     * 
     * Assert.assertEquals(f1.getSomeInt(), f2.getSomeInt()); Assert.assertEquals(f1.getSomeString(),
     * f2.getSomeString());
     * 
     * List<Bar> bar1 = f1.getSomeBar(); List<Bar> bar2 = f2.getSomeBar(); if(bar1!=null && bar2!=null) {
     * Assert.assertTrue(bar1.size() == bar2.size()); for(int i=0, size=bar1.size(); i<size; i++)
     * assertEquals(bar1.get(i), bar2.get(i)); }
     * 
     * 
     * Assert.assertEquals(f1.getSomeEnum(), f2.getSomeEnum()); Assert.assertEquals(f1.getSomeBytes(),
     * f2.getSomeBytes()); Assert.assertEquals(f1.getSomeBoolean(), f2.getSomeBoolean());
     * Assert.assertEquals(f1.getSomeFloat(), f2.getSomeFloat()); Assert.assertEquals(f1.getSomeDouble(),
     * f2.getSomeDouble()); Assert.assertEquals(f1.getSomeLong(), f2.getSomeLong()); }
     */

}
