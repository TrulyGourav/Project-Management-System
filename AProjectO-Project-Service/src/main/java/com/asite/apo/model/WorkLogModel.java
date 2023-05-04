package com.asite.apo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="project_tx_worklog_tbl")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "worklogId")
public class WorkLogModel {
	
	@Id
	@Column(name="worklog_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long worklogId;

	//Todo Link User
	@Column(name="user_id")
	private Long userId;

	//Link
	@Column(name="role_id")
	private Long roleId;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "task_id")
//	private TaskModel taskId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="version_id")
	private ProjectIterationModel projectVersionId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="task_id")
	private TaskModel taskId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="sub_task_id")
	private SubTaskModel subTaskId;
	
	@Column(name="date_worked")
	private LocalDate dateWorked;
	
	@Column(name="hours_worked")
	private Double hoursWorked;
	
	@Column(name="worklog_desc")
	private String worklogDesc;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	//	@Transient
//	private LocalTime startTime;
}
