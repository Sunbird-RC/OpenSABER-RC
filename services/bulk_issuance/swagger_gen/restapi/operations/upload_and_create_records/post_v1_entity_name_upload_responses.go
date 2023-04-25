// Code generated by go-swagger; DO NOT EDIT.

package upload_and_create_records

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	"net/http"

	"github.com/go-openapi/runtime"

	"bulk_issuance/swagger_gen/models"
)

// PostV1EntityNameUploadOKCode is the HTTP code returned for type PostV1EntityNameUploadOK
const PostV1EntityNameUploadOKCode int = 200

/*PostV1EntityNameUploadOK OK

swagger:response postV1EntityNameUploadOK
*/
type PostV1EntityNameUploadOK struct {

	/*
	  In: Body
	*/
	Payload models.CreateRecordResponse `json:"body,omitempty"`
}

// NewPostV1EntityNameUploadOK creates PostV1EntityNameUploadOK with default headers values
func NewPostV1EntityNameUploadOK() *PostV1EntityNameUploadOK {

	return &PostV1EntityNameUploadOK{}
}

// WithPayload adds the payload to the post v1 entity name upload o k response
func (o *PostV1EntityNameUploadOK) WithPayload(payload models.CreateRecordResponse) *PostV1EntityNameUploadOK {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the post v1 entity name upload o k response
func (o *PostV1EntityNameUploadOK) SetPayload(payload models.CreateRecordResponse) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *PostV1EntityNameUploadOK) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(200)
	payload := o.Payload
	if err := producer.Produce(rw, payload); err != nil {
		panic(err) // let the recovery middleware deal with this
	}
}

// PostV1EntityNameUploadNotFoundCode is the HTTP code returned for type PostV1EntityNameUploadNotFound
const PostV1EntityNameUploadNotFoundCode int = 404

/*PostV1EntityNameUploadNotFound Not found

swagger:response postV1EntityNameUploadNotFound
*/
type PostV1EntityNameUploadNotFound struct {

	/*
	  In: Body
	*/
	Payload string `json:"body,omitempty"`
}

// NewPostV1EntityNameUploadNotFound creates PostV1EntityNameUploadNotFound with default headers values
func NewPostV1EntityNameUploadNotFound() *PostV1EntityNameUploadNotFound {

	return &PostV1EntityNameUploadNotFound{}
}

// WithPayload adds the payload to the post v1 entity name upload not found response
func (o *PostV1EntityNameUploadNotFound) WithPayload(payload string) *PostV1EntityNameUploadNotFound {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the post v1 entity name upload not found response
func (o *PostV1EntityNameUploadNotFound) SetPayload(payload string) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *PostV1EntityNameUploadNotFound) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(404)
	payload := o.Payload
	if err := producer.Produce(rw, payload); err != nil {
		panic(err) // let the recovery middleware deal with this
	}
}

// PostV1EntityNameUploadInternalServerErrorCode is the HTTP code returned for type PostV1EntityNameUploadInternalServerError
const PostV1EntityNameUploadInternalServerErrorCode int = 500

/*PostV1EntityNameUploadInternalServerError Internal Server Error

swagger:response postV1EntityNameUploadInternalServerError
*/
type PostV1EntityNameUploadInternalServerError struct {

	/*
	  In: Body
	*/
	Payload string `json:"body,omitempty"`
}

// NewPostV1EntityNameUploadInternalServerError creates PostV1EntityNameUploadInternalServerError with default headers values
func NewPostV1EntityNameUploadInternalServerError() *PostV1EntityNameUploadInternalServerError {

	return &PostV1EntityNameUploadInternalServerError{}
}

// WithPayload adds the payload to the post v1 entity name upload internal server error response
func (o *PostV1EntityNameUploadInternalServerError) WithPayload(payload string) *PostV1EntityNameUploadInternalServerError {
	o.Payload = payload
	return o
}

// SetPayload sets the payload to the post v1 entity name upload internal server error response
func (o *PostV1EntityNameUploadInternalServerError) SetPayload(payload string) {
	o.Payload = payload
}

// WriteResponse to the client
func (o *PostV1EntityNameUploadInternalServerError) WriteResponse(rw http.ResponseWriter, producer runtime.Producer) {

	rw.WriteHeader(500)
	payload := o.Payload
	if err := producer.Produce(rw, payload); err != nil {
		panic(err) // let the recovery middleware deal with this
	}
}