package gov.loc.repository.workflow.actionhandlers;

//import static gov.loc.repository.workflow.constants.NdnpFixtureConstants.NDNP_NORMALIZED_PACKAGE_ID1;
//import static gov.loc.repository.workflow.constants.NdnpFixtureConstants.NDNP_REPOSITORY_ID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.agents.Person;
//import gov.loc.repository.packagemodeler.batch.Batch;
//import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.filelocation.IngestEvent;
//import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
//import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.util.Calendar;
import java.util.Iterator;
import java.text.MessageFormat;

public class IngestPackageEventActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(IngestPackageEventActionHandler.class);
	private Class eventClass;

	@ConfigurationField
	public String eventClassName;

	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;

	@ContextVariable(name="stagingPackageLocation")
	public String stagingPackageLocation;
	
	@ContextVariable(name="stagingStorageSystemId")
	public String stagingStorageSystemId;
	
	@Override
	protected void initialize() throws Exception
	{
		this.eventClass = Class.forName(eventClassName);
	}
		
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		
		Package packge = this.getDAO().findRequiredPackage(Package.class, this.repositoryId, this.packageId);				
		FileLocation ingestFileLocation = packge.getFileLocation(this.stagingStorageSystemId, this.stagingPackageLocation);
		if (ingestFileLocation == null)
		{
			throw new Exception(MessageFormat.format("Ingest File Location with staging storage system id {0} and package location {1} is not found for package {2} from repository {3}", this.stagingStorageSystemId, this.stagingPackageLocation, this.packageId, this.repositoryId));
		}

		IngestEvent event = (IngestEvent) this.getFactory().createFileLocationEvent(this.eventClass, ingestFileLocation, Calendar.getInstance().getTime(), this.getWorkflowAgent());

		TaskInstance taskInstance = this.executionContext.getTaskInstance();		

		//EventStart
		if (taskInstance.getStart() != null)
		{
			Calendar start = Calendar.getInstance();
			start.setTime(taskInstance.getStart());
			event.setEventStart(start.getTime());
		}
		//EventEnd
		if (taskInstance.getEnd() != null)
		{
			Calendar end = Calendar.getInstance();
			end.setTime(taskInstance.getEnd());
			event.setEventEnd(end.getTime());
		}
		//PerformingAgent
		event.setPerformingAgent(this.getDAO().findRequiredAgent(Person.class, taskInstance.getActorId()));
		//Success
		if (! "continue".equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}
		
		log.debug(MessageFormat.format("Adding Ingest Event to package {0}.  Event success: {1}", this.packageId, event.isSuccess()));		
	}
}
