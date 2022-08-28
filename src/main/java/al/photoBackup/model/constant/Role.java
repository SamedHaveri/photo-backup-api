package al.photoBackup.model.constant;

public enum Role {
	ROLE_ADMIN,
	ROLE_SYSADMIN,
	ROLE_USER,
	;

	public static Role getRoleFromName(String roleName) {
		switch (roleName) {
			case "ROLE_ADMIN":
				return ROLE_ADMIN;
			case "ROLE_USER":
				return ROLE_USER;
			case "ROLE_SYSADMIN":
				return ROLE_SYSADMIN;
			default:
				return null;
		}
	}

	Role() {
	}
}
