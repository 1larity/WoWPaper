CREATE TABLE `api_render_connection_details` (
	`_id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	`geo_zone_name`	TEXT NOT NULL UNIQUE,
	`url`	TEXT NOT NULL UNIQUE
)