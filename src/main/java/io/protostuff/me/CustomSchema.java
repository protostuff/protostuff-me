//========================================================================
//Copyright 2007-2010 David Yu dyuproject@gmail.com
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

import java.io.IOException;

/**
 * A schema (helper class) that wraps another schema and allows its subclasses to override certain methods for more
 * customization.
 * 
 * @author David Yu
 * @created May 28, 2010
 */
public abstract class CustomSchema/* <T> */implements Schema/* <T> */
{

    protected final Schema/* <T> */schema;

    public CustomSchema(Schema/* <T> */schema)
    {
        this.schema = schema;
    }

    public String getFieldName(int number)
    {
        return schema.getFieldName(number);
    }

    public int getFieldNumber(String name)
    {
        return schema.getFieldNumber(name);
    }

    public boolean isInitialized(Object message)
    {
        return schema.isInitialized(message);
    }

    public void mergeFrom(Input input, Object message) throws IOException
    {
        schema.mergeFrom(input, message);
    }

    public String messageFullName()
    {
        return schema.messageFullName();
    }

    public String messageName()
    {
        return schema.messageName();
    }

    public Object newMessage()
    {
        return schema.newMessage();
    }

    public Class/* <? super T> */typeClass()
    {
        return schema.typeClass();
    }

    public void writeTo(Output output, Object message) throws IOException
    {
        schema.writeTo(output, message);
    }

}
