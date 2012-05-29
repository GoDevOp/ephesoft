/********************************************************************************* 
* Ephesoft is a Intelligent Document Capture and Mailroom Automation program 
* developed by Ephesoft, Inc. Copyright (C) 2010-2011 Ephesoft Inc. 
* 
* This program is free software; you can redistribute it and/or modify it under 
* the terms of the GNU Affero General Public License version 3 as published by the 
* Free Software Foundation with the addition of the following permission added 
* to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED WORK 
* IN WHICH THE COPYRIGHT IS OWNED BY EPHESOFT, EPHESOFT DISCLAIMS THE WARRANTY 
* OF NON INFRINGEMENT OF THIRD PARTY RIGHTS. 
* 
* This program is distributed in the hope that it will be useful, but WITHOUT 
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
* FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more 
* details. 
* 
* You should have received a copy of the GNU Affero General Public License along with 
* this program; if not, see http://www.gnu.org/licenses or write to the Free 
* Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 
* 02110-1301 USA. 
* 
* You can contact Ephesoft, Inc. headquarters at 111 Academy Way, 
* Irvine, CA 92617, USA. or at email address info@ephesoft.com. 
* 
* The interactive user interfaces in modified source and object code versions 
* of this program must display Appropriate Legal Notices, as required under 
* Section 5 of the GNU Affero General Public License version 3. 
* 
* In accordance with Section 7(b) of the GNU Affero General Public License version 3, 
* these Appropriate Legal Notices must retain the display of the "Ephesoft" logo. 
* If the display of the logo is not reasonably feasible for 
* technical reasons, the Appropriate Legal Notices must display the words 
* "Powered by Ephesoft". 
********************************************************************************/ 

package com.ephesoft.dcma.gwt.admin.bm.client.presenter.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ephesoft.dcma.gwt.admin.bm.client.AdminConstants;
import com.ephesoft.dcma.gwt.admin.bm.client.BatchClassManagementController;
import com.ephesoft.dcma.gwt.admin.bm.client.MessageConstants;
import com.ephesoft.dcma.gwt.admin.bm.client.presenter.AbstractBatchClassPresenter;
import com.ephesoft.dcma.gwt.admin.bm.client.view.batch.ImportBatchClassView;
import com.ephesoft.dcma.gwt.core.client.ui.ScreenMaskUtility;
import com.ephesoft.dcma.gwt.core.shared.BatchClassDTO;
import com.ephesoft.dcma.gwt.core.shared.BatchFolderListDTO;
import com.ephesoft.dcma.gwt.core.shared.ConfirmationDialog;
import com.ephesoft.dcma.gwt.core.shared.ConfirmationDialogUtil;
import com.ephesoft.dcma.gwt.core.shared.ImportBatchClassSuperConfig;
import com.ephesoft.dcma.gwt.core.shared.ConfirmationDialog.DialogListener;
import com.ephesoft.dcma.gwt.core.shared.importTree.Node;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Ephesoft
 *
 */
/**
 * Presenter for Importing a batch class.
 */
public class ImportBatchClassPresenter extends AbstractBatchClassPresenter<ImportBatchClassView> {

	/**
	 * The batch class that is to being imported.
	 */
	private BatchFolderListDTO batchFolderListDTO;

	private final List<Node> uiConfigList = new ArrayList<Node>();

	public ImportBatchClassPresenter(final BatchClassManagementController controller, final ImportBatchClassView view) {
		super(controller, view);
	}

	@Override
	public void bind() {
	}

	public void showBatchClassImportView() {
		view.getDialogBox().setWidth("100%");
		view.getDialogBox().add(view);
		view.getDialogBox().center();
		view.getDialogBox().show();
		view.getDialogBox().setText(MessageConstants.BATCH_CLASS_IMPORT);
	}

	@Override
	public void injectEvents(final HandlerManager eventBus) {
		// Event handling to be done here.
	}

	public BatchFolderListDTO getBatchFolderListDTO() {
		return batchFolderListDTO;
	}

	public void setBatchFolderListDTO(BatchFolderListDTO batchFolderListDTO) {
		this.batchFolderListDTO = batchFolderListDTO;
	}

	public List<Node> getUiConfigList() {
		return uiConfigList;
	}

	public void onSubmitComplete() {
		ConfirmationDialog confirmationDialog = ConfirmationDialogUtil.showConfirmationDialog(
				MessageConstants.BATCH_CLASS_IMPORTED_SUCCESSFULLY, MessageConstants.IMPORT_SUCCESSFUL, Boolean.TRUE);

		confirmationDialog.addDialogListener(new DialogListener() {

			@Override
			public void onOkClick() {
				controller.getMainPresenter().init();
				view.getDialogBox().hide(true);
			}

			@Override
			public void onCancelClick() {
				// do nothing.
			}
		});

	}

	public void onOkClicked(final boolean isUseSource, final boolean isImportExisting) {
		boolean validCheck = true;
		if (!isUseSource) {
			boolean isPriorityNumber = isNumber(view.getPriority());

			if (validCheck
					&& (!view.getValidateTextBox().validate() || !view.getValidateDescTextBox().validate() || !view
							.getValidateNameTextBox().validate())) {
				ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.MANDATORY_FIELDS_ERROR_MSG);
				ScreenMaskUtility.unmaskScreen();
				validCheck = false;
			}
			if (validCheck && !isPriorityNumber) {
				ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.PRIORITY_SHOULD_BE_NUMERIC);
				ScreenMaskUtility.unmaskScreen();
				validCheck = false;
			}
			if (validCheck
					&& (Integer.parseInt(view.getPriority()) < AdminConstants.PRIORITY_LOWER_LIMIT || Integer.parseInt(view
							.getPriority()) > AdminConstants.PRIORITY_UPPER_LIMIT)) {
				ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.PRIORITY_SHOULD_BE_BETWEEN_1_AND_100);
				ScreenMaskUtility.unmaskScreen();
				validCheck = false;
			}

			if (validCheck && (view.getName().indexOf(" ") > -1 || view.getName().indexOf("-") > -1)) {
				ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.WORKFLOW_IMPROPER_CHARACTER);
				ScreenMaskUtility.unmaskScreen();
				validCheck = false;
			}
		}

		if (validCheck && !isImportExisting && !view.getValidateUNCTextBox().validate()) {
			ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.MANDATORY_FIELDS_ERROR_MSG);
			ScreenMaskUtility.unmaskScreen();
			validCheck = false;
		}

		if (validCheck) {
			controller.getRpcService().getAllBatchClassesIncludingDeleted(new AsyncCallback<List<BatchClassDTO>>() {

				@Override
				public void onFailure(final Throwable arg0) {
					ScreenMaskUtility.unmaskScreen();
					ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.UNC_PATH_NOT_VERIFIED);
				}

				@Override
				public void onSuccess(final List<BatchClassDTO> batches) {
					boolean isUNCPathUnique = true;
					boolean isNameExistInDatabase = false;
					// Perform validation on batchClass UNC path (should not exist in Database)
					for (BatchClassDTO batchDTO : batches) {
						if (batchDTO.getUncFolder().equalsIgnoreCase(view.getUncFolder())) {
							isUNCPathUnique = false;
						}

						// Perform validation on batchClass "Name" (Should exist in database)
						if (isNameExistInDatabase || batchDTO.getName().equalsIgnoreCase(view.getName())) {
							isNameExistInDatabase = true;
						}
					}
					final boolean isNameExistInDatabaseFinal = isNameExistInDatabase;

					if (!isImportExisting) {
						// new batch class
						// UNC path should be unique
						if (!isUNCPathUnique) {
							ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.UNC_PATH_NOT_UNIQUE);
							ScreenMaskUtility.unmaskScreen();
							return;
						}
					}
					controller.getRpcService().isWorkflowContentEqual(view.getImportBatchClassUserOptionDTO(), view.getName(),
							new AsyncCallback<Map<String, Boolean>>() {

								@Override
								public void onFailure(Throwable arg0) {
									// TODO Auto-generated method stub

								}

								public void onSuccess(Map<String, Boolean> results) {
									boolean isContentEqual = results.get("isEqual");
									boolean isDeployed = results.get("isDepoyed");
									view.getImportBatchClassUserOptionDTO().setWorkflowDeployed(isDeployed);
									// verify the contents are equal to the deployed workflow
									if (isNameExistInDatabaseFinal && !isContentEqual) {
										ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.WORKFLOWNAME_EXISTS);
										ScreenMaskUtility.unmaskScreen();
										return;
									}
									// verify the contents are equal to the deployed workflow
									if (!isNameExistInDatabaseFinal && isDeployed) {
										// warning
										// if yes, import with the specified workflow name.
										ConfirmationDialog cf = ConfirmationDialogUtil.showConfirmationDialog(
												MessageConstants.CONFIRM_IMPORT, MessageConstants.BATCH_CLASS_IMPORT, false, true);
										cf.addDialogListener(new DialogListener() {

											@Override
											public void onOkClick() {
												saveBatchClass();
											}

											@Override
											public void onCancelClick() {
												ScreenMaskUtility.unmaskScreen();
												return;
											}
										});
									} else {
										saveBatchClass();
									}
								}
							});

					/*
					 * if ((!isImportExisting || !view.getImportBatchClassUserOptionDTO().isWorkflowDeployed()) && !isUNCPathUnique) {
					 * ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.UNC_PATH_NOT_UNIQUE);
					 * ScreenMaskUtility.unmaskScreen(); return; }
					 * 
					 * if (!view.getImportBatchClassUserOptionDTO().isWorkflowDeployed() && isNameExistInDatabase ||
					 * view.getName().isEmpty()) {
					 * ConfirmationDialogUtil.showConfirmationDialogError("Batch name should not be any of the existing batch name.");
					 * ScreenMaskUtility.unmaskScreen(); return; } if (isImportExisting && !isNameExistInDatabase) {
					 * ConfirmationDialogUtil.showConfirmationDialogError("Batch name should MATCH any of the existing batch name.");
					 * ScreenMaskUtility.unmaskScreen(); return; }
					 */
				}
			});
		} else if (validCheck && isImportExisting) {
			saveBatchClass();
		}

	}

	private void saveBatchClass() {
		view.getImportBatchClassUserOptionDTO().setName(view.getName());
		controller.getRpcService().importBatchClass(view.getImportBatchClassUserOptionDTO(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(final Boolean isSuccess) {
				ScreenMaskUtility.unmaskScreen();
				if (isSuccess) {
					ConfirmationDialogUtil.showConfirmationDialog(MessageConstants.BATCH_CLASS_IMPORTED_SUCCESSFULLY,
							MessageConstants.BATCH_CLASS_IMPORT, Boolean.TRUE);
					controller.getMainPresenter().init();
				} else {
					ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.IMPORT_UNSUCCESSFUL);
				}
				view.getDialogBox().hide(true);

			}

			@Override
			public void onFailure(final Throwable arg0) {
				ScreenMaskUtility.unmaskScreen();
				ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.IMPORT_UNSUCCESSFUL);
				view.getDialogBox().hide(true);
			}

		});
	}

	public void setFolderList() {
		view.getbatchFolderListView(uiConfigList);
	}

	private boolean isNumber(final String priority) {
		boolean returnValue = true;
		try {
			Integer.parseInt(priority);
		} catch (NumberFormatException e) {
			returnValue = false;
		}
		return returnValue;
	}

	public void onAttachComplete(final String workFlowName, final String lastAttachedZipSourcePath) {

		controller.getRpcService().getImportBatchClassUIConfig(workFlowName, lastAttachedZipSourcePath,
				new AsyncCallback<ImportBatchClassSuperConfig>() {

					@Override
					public void onFailure(Throwable arg0) {
						ConfirmationDialogUtil.showConfirmationDialogError(arg0.getMessage());
					}

					@Override
					public void onSuccess(ImportBatchClassSuperConfig importBatchClassSuperConfig) {
						if (importBatchClassSuperConfig == null) {
							view.getDialogBox().hide();
							ConfirmationDialogUtil.showConfirmationDialogError(MessageConstants.IMPORT_UNSUCCESSFUL);
							return;
						}
						view.setUNCFolderList(importBatchClassSuperConfig.getUncFolderList());
						if (!view.getImportBatchClassUserOptionDTO().isWorkflowEqual()
								|| !view.getImportBatchClassUserOptionDTO().isWorkflowExistsInBatchClass()) {
							view.toggleUseExistingState(false);
							view.getErrorMessage().setText(MessageConstants.WORKFLOWNAME_EXISTS);
						} else {
							view.toggleUseExistingState(true);
							view.getErrorMessage().setText("");
						}
						uiConfigList.clear();
						uiConfigList.addAll(importBatchClassSuperConfig.getUiConfigRoot().getChildren());
						setFolderList();
					}
				});

	}

	public void deleteAttachedFolders(final String lastAttachedZipSourcePath) {
		controller.getRpcService().deleteAttachedFolders(lastAttachedZipSourcePath, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Void arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
