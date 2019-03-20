module se.uu.ub.cora.userpicker {
	requires se.uu.ub.cora.gatekeeper;

	provides se.uu.ub.cora.gatekeeper.user.UserPickerProvider
			with se.uu.ub.cora.userpicker.UserInStorageUserPickerProvider;
}