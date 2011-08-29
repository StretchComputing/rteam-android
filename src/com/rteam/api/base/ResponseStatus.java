package com.rteam.api.base;

import com.rteam.api.common.StringUtils;

public enum ResponseStatus {
	Unknown(-1), 
	NoResponse(-2),
	NoResponseString(-3),
	InvalidJSON(-4),
	Success(100),
	InvalidUserCredentials(200, "Invalid user credentials, please try again."),
	EmailAddressAlreadyUsed(201, "Specified email address is already in use."),
	UserNotMemberOfSpecifiedTeam(204, "User not a member of the specified team."),
	UserNotCoordinator(205, "Current user is not coordinator and is unable to make specified changes."),
	UserNotCoordinatorNorOwningMember(206, "Current user is not a coordinator and is unable to make the specified changes."),
	UserNotACoordinatorOrMemberParticipant(207, "Current user is not a coordinator or member participant."),
	UserNotNetworkAuthenticated(208, "User is not network authenticated."),
	MemberEmailNotUnique(209, "Member email has already been used, please verify it is correct and try again."),
	UserCannotDeleteSelf(211, "User cannot delete self."),
	CreatorParticipantRoleCannotBeChanged(212, "Creator's role cannot be changed."),
	UserNotOriginatorOrRecipientOfMessageThread(218, "Current user cannot access specified message thread."),
	PasswordResetFailed(215, "Password reset failed, please try again."),
	EmailConfirmationLinkNoLongerActive(216, "Email confirmation link is no longer active."),
	DeleteMemberConfirmationLinkNoLongerActive(217, "Delete member confirmation link is no longer active."),
	GuardianEmailNotUnique(219, "Guardian email has already been used, please verify it is correct and try agian."),
	TwitterError(220, "Error authenticating with twitter, please try again."),
	MemberCannotBeAFan(221, "Members cannot also be fans."),
	MemberPhoneNumberNotUnique(222, "Member phone number is not unique, please verify it is correct and try again."),
	UserNotCreatorNorNetworkAuthenticated(223, "User is not creator nor network authenticated, cannot add member."),
	EmailAddressCannotBeUpdated(224, "Email address cannot be updated."),
	GuardianPhoneNumberNotUnique(225, "Guardian phone number is not unique, please verify it is correct and try again."),
	GuardianEmailAddressCannotBeUpdated(226, "Guardian email address cannot be updated."),
	PhoneNumberCannotBeUpdated(228, "Phone number cannot be updated."),
	GuardianPhoneNumberCannotBeUpdated(229, "Guardian phone number cannot be updated."),
	FirstAndLastNamesRequired(300, "First and last name are required."),
	EmailAddressAndPasswordRequired(303, "Email address and password are required."),
	TeamNameAndDescriptionRequired(304, "Team name and description are both required."),
	TeamIdRequired(305, "Team ID is required."),
	TeamIdGameIdAndTimeZoneRequired(307, "Team Id, Game Id, and Time zone are required."),
	TeamIdAndGameIdRequired(308, "Team Id and Game Id are required."),
	TeamIdPracticeIdAndTimeZoneRequired(310, "Team Id, Practice Id, and Time Zone are required."),
	AllParametersRequired(311, "All parameters are required."),
	SubjectBodyAndTypeRequired(312, "Subject body and type are required."),
	TimeZoneRequired(313, "Time zone is required."),
	TeamIdMessageThreadIdAndTimeZoneRequired(314, "Team Id, Message Thread Id, and Time zone are required."),
	TeamIdAndMessageThreadIdRequired(315, "Team Id and Message Thread Id are required."),
	EitherEventIdAndMemberIdRequired(316, "Event Id or Member Id are required."),
	EventTypeRequired(317, "Event type is required."),
	StatusUpdateOrPhotoRequired(318, "Status update or Photo required."),
	DateIntervalRequired(320, "Date interval required."),
	ActivityIdsRequired(321, "Activity Ids required."),
	ActivityIdRequired(322, "Activity Id required."),
	VoteRequired(323, "Vote is required."),
	StartDateRequired(325, "Start date is required."),
	MemberIdRequired(326, "Member Id is required."),
	FirstNameOrLastNameOrEmailAddressOrPhoneNumberRequired(327, "Must specify at least one of the following: First Name, Last Name, Email Address, or Phone Number."),
	LatitudeAndLongitudeMustBeSpecifiedTogether(400, "Latitude and Longitude need to be specified together."),
	TimeZoneAndDatesMustBeSpecifiedTogether(401, "Time zone and dates must be specified together."),
	TimeZoneAndMemberIdMustBeSpecifiedTogether(402, "Time zone and member id must be specified together."),
	EventIdAndEventTypeMustBeSpecifiedTogether(403, "Must specify event id and event type together."),
	PollAndPollChoicesMustBeSpecifiedTogether(405, "Must specify poll and poll choices together."),
	TeamIdMustBeSpecifiedWithEventId(406, "Team Id must be specified with Event Id."),
	VideoAndPhotoMustBeSpecifiedTogether(407, "Video and photo must be specified together."),
	IsPortraitAndPhotoMustBeSpecifiedTogether(408),
	RefreshFirstAndNewOnlyMustBeSpecifiedTogether(409, "Refresh first and new only must be specified together."),
	InvalidIsNetworkAuthenticatedParameter(500, "Network authenticated parameter is invalid."),
	InvalidStateParameter(501, "Invalid state parameter."),
	InvalidGenderParameter(502, "Invalid gender parameter."),
	InvalidLatitudeParameter(503, "Latitutde parameter is invalid."),
	InvalidLongitudeParameter(504, "Longitude parameter is invalid."),
	InvalidParticipantRoleParameter(505, "Invalid participant role parameter."),
	InvalidAgeParameter(506, "Invalid age parameter."),
	InvalidIncludeFansParameter(507, "Invalid include fans."),
	InvalidNotificationTypeParameter(508, "Invalid notification type parameter."),
	InvalidTimeZoneParameter(509, "Invalid timezone parameter."),
	InvalidStartDateParameter(510, "Invalid start date parameter."),
	InvalidEndDateParameter(511, "Invalid end date parameter."),
	InvalidTypeParameter(512, "Invalid type parameter."),
	InvalidStatusParameter(513, "Invalid status parameter."),
	InvalidEventTypeParameter(515, "Invalid EventType parameter."),
	InvalidIncludeBodyAndChoiceParameter(516),
	InvalidWasViewedParameter(517),
	InvalidAlreadyMemberParameter(518),
	InvalidNumberOfPollChoicesParameter(522, "Invalid number of poll choices parameter."),
	InvalidMessageLocation(523, "Invalid message location parameter."),
	InvalidHappeningParameter(524, "Invalid happening parameter."),
	InvalidRefreshFirstParameter(526, "Invalid refresh first parameter."),
	InvalidNewOnlyParameter(527, "Invalid new only parameter."),
	InvalidMaxCountParameter(528, "Invalid max count parameter."),
	InvalidMaxCacheIdParameter(529, "Invalid max cache id parameter."),
	InvalidMostCurrentDateParameter(530, "Invalid most current date parameter."),
	InvalidTotalNumberOfDaysParameter(531, "Invalid total number of days parameter."),
	InvalidStatusUpdateMaxSizeExceeded(532, "Max file size exceeded."),
	InvalidResyncCounterParameter(533),
	InvalidIncludeNewActivityParameter(534, "Invalid include new activity parameter."),
	InvalidVoteParameter(535, "Invalid vote parameter."),
	InvalidPhotoParameter(536, "Invalid photo."),
	InvalidAutoArchiveDayCountParameter(537),
	InvalidUseThreadParameter(538),
	InvalidVoteTypeParameber(539, "Invalid vote type parameter."),
	InvalidUpdateAllParameter(591, "Invalid update all parameter."),
	InvalidPhoneNumberParameter(542, "Invalid phone number."),
	InvalidIsCancelledParameter(543, "Invalid is cancelled parameter."),
	InvalidGuardianPhoneNumberParameter(544, "Invalid guardian phone number."),
	InvalidGuardianKeyParameter(545, "Invalid guardian key parameter."),
	InvalidMobileCarrierCodeParameter(546, "Invalid mobile carrier code."),
	InvalidConfirmationCodeParameter(547, "Invalid confirmation code."),
	UserNotFound(600, "Unable to find specified user."),
	MemberNotFound(601, "Unable to find specified member."),
	TeamNotFound(602, "Unable to find specified team."),
	GameNotFound(603, "Unable to find specified game."),
	PracticeNotFound(604, "Unable to find specified practice."),
	MessageThreadNotFound(605, "Message thread not found"),
	MembershipOfUserNotFound(606, "Membership information of user not found."),
	ActivityNotFound(607, "Unable to find specified activity."),
	EventNotFound(608, "Unable to find specified event."),
	EventIdAndMemberIdMutuallyExclusive(700, "Event Id and Member Id are mutually exclusive."),
	MutuallyExclusiveParametersSpecified(701, "Mutually exclusive parameters specified."),
	RefreshFirstAndMaxCacheIdMutuallyExclusive(703, "Refresh first and max cache id mutually exclusive."),
	NewOnlyAndDateInternalMutuallyExclusive(704, "New only and date internal mutually exclusive."),
	PhotoAndThumbnailMutuallyExclusive(705, "Photo and thumbnail are mutually exclusive."),
	MessageThreadConfirmationLinkNoLongerActive(800, "Message thread confirmation link no longer active.")
	;
	
	private int _code;
	private String _errorMessage;
	
	private ResponseStatus(int code) { this(code, null); }
	private ResponseStatus(int code, String errorMessage) {
		_code = code;
		_errorMessage = errorMessage;
	}
	
	public int getCode() { return _code; }
	public String getErrorMessage() { return _errorMessage; }
	public boolean hasErrorMessage() { return !StringUtils.isNullOrEmpty(_errorMessage); }
	
	
	public static ResponseStatus valueOfCode(String value) {
		int code = -1;
		try {
			code = Integer.parseInt(value);
		} catch(NumberFormatException e) {}
		
		for (ResponseStatus status : ResponseStatus.values()) {
			if (status.getCode() == code) {
				return status;
			}
		}
		return ResponseStatus.Unknown;
	}
}
