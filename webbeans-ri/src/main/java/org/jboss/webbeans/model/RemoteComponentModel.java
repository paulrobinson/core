package org.jboss.webbeans.model;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.webbeans.BoundTo;
import javax.webbeans.Destroys;

import org.jboss.webbeans.ContainerImpl;
import org.jboss.webbeans.injectable.ComponentConstructor;
import org.jboss.webbeans.injectable.EnterpriseConstructor;
import org.jboss.webbeans.injectable.InjectableMethod;
import org.jboss.webbeans.introspector.AnnotatedType;

public class RemoteComponentModel<T> extends AbstractEnterpriseComponentModel<T>
{
   
   private EnterpriseConstructor<T> constructor;
   private String boundTo;
   private String location;
   
   public RemoteComponentModel(AnnotatedType annotatedItem,
         AnnotatedType xmlAnnotatedItem, ContainerImpl container)
   {
      super(annotatedItem, xmlAnnotatedItem);
      init(container);
   }
   
   @Override
   protected void init(ContainerImpl container)
   {
      super.init(container);
      // TODO initialize constructor
      initBoundTo();
      initRemoveMethod();
   }
   
   protected void initRemoveMethod()
   {
      Set<Method> destroyMethods = new HashSet<Method>();
      for (Method method : getAnnotatedItem().getDelegate().getMethods())
      {
         if (method.isAnnotationPresent(Destroys.class))
         {
            destroyMethods.add(method);
         }
      }
      if (destroyMethods.size() == 1)
      {
         Method destroyMethod = destroyMethods.iterator().next();
         if (destroyMethod.getParameterTypes().length > 0)
         {
            throw new RuntimeException(getLocation() + " The method annotated @Destroys cannot take any parameters");
         }
         else
         {
            removeMethod = new InjectableMethod(destroyMethod);
         }
      }
      else if (destroyMethods.size() > 1)
      {
         // TODO Enumerate the destroy methods
         throw new RuntimeException(getLocation() + " There can only be a maximum of one method declared @Destorys");
      }
         
   }
   
   @Override
   protected void checkComponentImplementation()
   {
      // No - op for remote components
      // TODO THis is wrong probably
   }
   
   protected void initBoundTo()
   {
      if (getXmlAnnotatedItem().isAnnotationPresent(BoundTo.class))
      {
         this.boundTo = getXmlAnnotatedItem().getAnnotation(BoundTo.class).value();
         return;
      }
      if (getAnnotatedItem().isAnnotationPresent(BoundTo.class))
      {
         this.boundTo = getAnnotatedItem().getAnnotation(BoundTo.class).value();
         return;
      }
      throw new RuntimeException("Remote component doesn't specify @BoundTo or <bound-to /> for " + getType());
   }
   
   public ComponentConstructor<T> getConstructor()
   {
      return constructor;
   }
   

   public String getBoundTo()
   {
      return boundTo;
   }
   
   @Override
   public String toString()
   {
      return "RemoteComponentModel[" + getType().getName() + "]";
   }

   @Override
   public String getLocation()
   {
      if (location == null)
      {
         location = "type: Remote Enterprise Component; declaring class: " + getType() +";";
      }
      return location;
   }

}
