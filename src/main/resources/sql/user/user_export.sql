SELECT
    u.id,
    u.username,
    u.name,
    u.email,
    u.phone_number as phoneNumber,
    u.gender,
    u.age,
    u.dob,
    u.code,
    u.last_modified_by as lastModifiedBy,
    u.last_modified_date as lastModifiedDate,
    GROUP_CONCAT(r.code) as roles
FROM
    user u
        LEFT JOIN user_roles ur ON
        u.id = ur.user_id
        LEFT JOIN role r ON
        ur.roles_id = r.id
GROUP BY
    u.id