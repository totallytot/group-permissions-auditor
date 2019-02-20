package com.totallytot.email

import com.totallytot.ao.AuditReport

class AuditNotification {

    private final List<AuditReport> auditReportEntities

    AuditNotification(List<AuditReport> auditReportEntities) {
        this.auditReportEntities = auditReportEntities
    }

    String compoundHTMLNotification() {
        def style = '<style>                                  ' +
                    'table {                                  ' +
                    '  font-family: arial, sans-serif;        ' +
                    '  border-collapse: collapse;             ' +
                    '  width: 100%;                           ' +
                    '}                                        ' +
                    'td, th {                                 ' +
                    '  border: 1px solid #dddddd;             ' +
                    '  text-align: left;                      ' +
                    '  padding: 8px;                          ' +
                    '}                                        ' +
                    'tr:nth-child(even) {                     ' +
                    '  background-color: #dddddd;             ' +
                    '}                                        ' +
                    '</style>                                 '

        def table = '<table>                                  ' +
                    '   <tr>                                  ' +
                    '       <th>Space Key</th>                ' +
                    '       <th>Group</th>                    ' +
                    '       <th>Permission</th>               ' +
                    '       <th>Violator</th>                 ' +
                    '       <th>Date</th>                     ' +
                    '   </tr>                                 '

        auditReportEntities.each {
            table+= '   <tr>                                  ' +
                    '       <td>' + it.spaceKey +   '</td>    ' +
                    '       <td>' + it.group +      '</td>    ' +
                    '       <td>' + it.permission + '</td>    ' +
                    '       <td>' + it.violator +   '</td>    ' +
                    '       <td>' + it.date +       '</td>    ' +
                    '   </tr>                                 '
                    }
                    '</table>                                 '
        '<!DOCTYPE html><html><head>' + style + '<head><body>' + table + '</body></html>'
    }
}