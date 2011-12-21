// Automatically Generated -- DO NOT EDIT
// com.google.gwt.sample.dynatablerf.shared.DynaTableRequestFactory
package com.google.gwt.sample.dynatablerf.shared;
import java.util.Arrays;
import com.google.web.bindery.requestfactory.vm.impl.OperationData;
import com.google.web.bindery.requestfactory.vm.impl.OperationKey;
public final class DynaTableRequestFactoryDeobfuscatorBuilder extends com.google.web.bindery.requestfactory.vm.impl.Deobfuscator.Builder {
{
withOperation(new OperationKey("Rvm1kRBIqadzRZRDxLBeb__sHZw="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/String;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/String;)V")
  .withMethodName("logMessage")
  .withRequestContext("com.google.web.bindery.requestfactory.shared.LoggingRequest")
  .build());
withOperation(new OperationKey("KkFhoVmD9tbbI$w2XRh7I__kE6U="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/InstanceRequest;")
  .withDomainMethodDescriptor("()V")
  .withMethodName("persist")
  .withRequestContext("com.google.gwt.sample.dynatablerf.shared.DynaTableRequestFactory$PersonRequest")
  .build());
withOperation(new OperationKey("8RPAa1igf$A0Nw5A3NLXb9xgTIg="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(III)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(III)Lcom/google/gwt/sample/dynatablerf/domain/TimeSlot;")
  .withMethodName("createTimeSlot")
  .withRequestContext("com.google.gwt.sample.dynatablerf.shared.DynaTableRequestFactory$ScheduleRequest")
  .build());
withOperation(new OperationKey("HrERz7H3WvoccGnCBuYtbWpBxrI="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(IILjava/util/List;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(IILjava/util/List;)Ljava/util/List;")
  .withMethodName("getPeople")
  .withRequestContext("com.google.gwt.sample.dynatablerf.shared.DynaTableRequestFactory$SchoolCalendarRequest")
  .build());
withOperation(new OperationKey("oUIRurBNaMwHnN2Mj_UxbWkKebc="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("()Lcom/google/gwt/sample/dynatablerf/domain/Person;")
  .withMethodName("getRandomPerson")
  .withRequestContext("com.google.gwt.sample.dynatablerf.shared.DynaTableRequestFactory$SchoolCalendarRequest")
  .build());
withRawTypeToken("DR7UfhFdIUIf81cyQWo86039Fg8=", "com.google.gwt.sample.dynatablerf.shared.AddressProxy");
withRawTypeToken("urRrn7703gWC4syU4SCz0KxRmcU=", "com.google.gwt.sample.dynatablerf.shared.PersonProxy");
withRawTypeToken("P_3Ow0fIixLW03HcZyWi4cvOX94=", "com.google.gwt.sample.dynatablerf.shared.ScheduleProxy");
withRawTypeToken("m0sGtgNooStU1adbHBX1dBlCaqo=", "com.google.gwt.sample.dynatablerf.shared.TimeSlotProxy");
withRawTypeToken("w1Qg$YHpDaNcHrR5HZ$23y518nA=", "com.google.web.bindery.requestfactory.shared.EntityProxy");
withRawTypeToken("8KVVbwaaAtl6KgQNlOTsLCp9TIU=", "com.google.web.bindery.requestfactory.shared.ValueProxy");
withRawTypeToken("FXHD5YU0TiUl3uBaepdkYaowx9k=", "com.google.web.bindery.requestfactory.shared.BaseProxy");
withClientToDomainMappings("com.google.gwt.sample.dynatablerf.domain.Address", Arrays.asList("com.google.gwt.sample.dynatablerf.shared.AddressProxy"));
withClientToDomainMappings("com.google.gwt.sample.dynatablerf.domain.Person", Arrays.asList("com.google.gwt.sample.dynatablerf.shared.PersonProxy"));
withClientToDomainMappings("com.google.gwt.sample.dynatablerf.domain.Schedule", Arrays.asList("com.google.gwt.sample.dynatablerf.shared.ScheduleProxy"));
withClientToDomainMappings("com.google.gwt.sample.dynatablerf.domain.TimeSlot", Arrays.asList("com.google.gwt.sample.dynatablerf.shared.TimeSlotProxy"));
}}
