package cassandra;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author b1r
 */
public class ArchiveException extends Exception {

	private static final long serialVersionUID = 1L;

	protected String errorContext = "UNDEFINED"; 
	protected String errorCode = "UNDEFINED";

	public static final String INVALID_DOCUMENT_OBJECT = "INVALID_DOCUMENT_OBJECT";
	public static final String INVALID_KEYSPACE = "INVALID_KEYSPACE";
	public static final String INVALID_WORKITEM = "INVALID_WORKITEM";
	public static final String MD5_ERROR = "MD5_ERROR";


	public static final String MISSING_CONTACTPOINT = "MISSING_CONTACTPOINT";
	public static final String SYNC_ERROR = "SYNC_ERROR";

	public ArchiveException(String aErrorCode, String message) {
		super(message);
		errorCode = aErrorCode;

	}

	public ArchiveException(String aErrorContext, String aErrorCode, String message) {
		super(message);
		errorContext = aErrorContext;
		errorCode = aErrorCode;

	}

	public ArchiveException(String aErrorContext, String aErrorCode, String message, Exception e) {
		super(message, e);
		errorContext = aErrorContext;
		errorCode = aErrorCode;

	}

	public ArchiveException(String aErrorCode, String message, Exception e) {
		super(message, e);

		errorCode = aErrorCode;

	}

	public String getErrorContext() {
		return errorContext;
	}

	public void setErrorContext(String errorContext) {
		this.errorContext = errorContext;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
