<?php

namespace App\Imports;

use App\Models\Answer;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class AnswerImport implements ToModel, WithHeadingRow
{
    /**
    * @param array $row
    *
    * @return \Illuminate\Database\Eloquent\Model|null
    */
    public function model(array $row)
    {
        return new Answer([
            'answer'        => $row['answer'],
            'marksAwarded'  => $row['marksawarded'],
            'answerBankID'  => $row['answerbankid'],
            'questionNo'    => $row['questionno'],
        ]);
    }
}