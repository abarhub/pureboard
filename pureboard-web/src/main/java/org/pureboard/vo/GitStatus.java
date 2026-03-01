package org.pureboard.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GitStatus {

    private String lastCommit;
    private LocalDateTime dateTimeLastCommit;
    private List<String> branches;
    private boolean clean;
    private String auteur;

}
