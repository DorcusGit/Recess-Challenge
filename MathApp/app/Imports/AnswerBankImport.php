<?php

namespace App\Imports;

use App\Models\AnswerBank;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class AnswerBankImport implements ToModel, WithHeadingRow
{
    /**
    * @param array $row
    *
    * @return \Illuminate\Database\Eloquent\Model|null
    */
    public function model(array $row)
    {
        return new AnswerBank([
            'challengeNo'         => $row['challengeno'],
            'questionBankID'      => $row['questionbankid'],
            'answerBank' => $row['answerbank'],
        ]);
    }
}