<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Answer extends Model
{
    use HasFactory;

    protected $table = 'answers'; // Table name

    // Define the primary key
    protected $primaryKey = 'answerNo';

    // Disable timestamps if not using created_at and updated_at columns
    public $timestamps = true;

    // Fillable properties
    protected $fillable = [
        'answer',
        'marksAwarded',
        'answerBankID',
        'questionNo',
    ];

    // Define the relationship with AnswerBank
    public function answerBank()
    {
        return $this->belongsTo(AnswerBank::class, 'answerBankID', 'answerBankID');
    }

    // Define the relationship with Question
    public function question()
    {
        return $this->belongsTo(Question::class, 'questionNo', 'questionNo');
    }
}
