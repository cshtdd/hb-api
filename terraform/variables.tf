variable "service" {
  default = "hb-api"
}

variable "stage" {
  default = "dev"
}

variable "aws_region" {
  default = "us-east-1"
}

variable "domainName" {
  default = "invalid.url"
}

variable "lambdaRuntime" {
  default = "java11"
}

variable "endpointType" {
  default = "regional"
}

variable "basePath" {
  default = ""
}

variable "dynamoDbEndpointOverride" {
  default = ""
}

locals {
  tablePrefix = "${var.service}-${var.stage}-"
}