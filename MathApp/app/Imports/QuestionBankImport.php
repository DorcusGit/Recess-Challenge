<?php

namespace App\Imports;

use App\Models\QuestionBank;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class QuestionBankImport implements ToModel, WithHeadingRow
{
    /**
    * @param array $row
    *
    * @return \Illuminate\Database\Eloquent\Model|null
    */
    public function model(array $row)
    {
        return new QuestionBank([
            'challengeNo'           => $row['challengeno'],
            'questionBank' => $row['questionbank'],
        ]);
    }
}