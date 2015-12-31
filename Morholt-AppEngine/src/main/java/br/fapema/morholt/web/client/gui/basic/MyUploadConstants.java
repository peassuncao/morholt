package br.fapema.morholt.web.client.gui.basic;

import gwtupload.client.IUploader;

public class MyUploadConstants implements IUploader.UploaderConstants {

	@Override
	public String uploadLabelCancel() {
		return "cancelar";
	}

	@Override
	public String uploadStatusCanceled() {
		return "cancelado";
	}

	@Override
	public String uploadStatusCanceling() {
		return "cancelando";
	}

	@Override
	public String uploadStatusDeleted() {
		return "removido";
	}

	@Override
	public String uploadStatusError() {
		return "erro no upload";
	}

	@Override
	public String uploadStatusInProgress() {
		return "em progresso";
	}

	@Override
	public String uploadStatusQueued() {
		return "na fila";
	}

	@Override
	public String uploadStatusSubmitting() {
		return "enviando";
	}

	@Override
	public String uploadStatusSuccess() {
		return "sucesso";
	}

	@Override
	public String uploaderActiveUpload() {
		return "ativo";
	}

	@Override
	public String uploaderAlreadyDone() {
		return "upload já feito";
	}

	@Override
	public String uploaderBlobstoreError() {
		return "erro no blobstore";
	}

	@Override
	public String uploaderBrowse() {
		return "browse";
	}

	@Override
	public String uploaderDrop() {
		return "soltar";
	}

	@Override
	public String uploaderInvalidExtension() {
		return "extensão inválida";
	}

	@Override
	public String uploaderSend() {
		return "enviar";
	}

	@Override
	public String uploaderServerError() {
		return "erro no servidor";
	}

	@Override
	public String submitError() {
		return "erro no envio";
	}

	@Override
	public String uploaderServerUnavailable() {
		return "servidor não disponível";
	}

	@Override
	public String uploaderTimeout() {
		return "timeout";
	}

	@Override
	public String uploaderBadServerResponse() {
		return "falha no servidro";
	}

	@Override
	public String uploaderBlobstoreBilling() {
		return "uploaderBlobstoreBilling";
	}

	@Override
	public String uploaderInvalidPathError() {
		return "caminho inválido";
	}

	@Override
	public String uploaderBadParsing() {
		return "falha de parse";
	}
}
