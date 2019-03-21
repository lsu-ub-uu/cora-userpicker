module se.uu.ub.cora.userpicker {
	requires transitive se.uu.ub.cora.gatekeeper;

	exports se.uu.ub.cora.userpicker;

	provides se.uu.ub.cora.gatekeeper.user.UserPickerProvider
			with se.uu.ub.cora.userpicker.UserInStorageUserPickerProvider;
}